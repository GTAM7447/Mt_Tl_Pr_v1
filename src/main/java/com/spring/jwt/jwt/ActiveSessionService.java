package com.spring.jwt.jwt;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ActiveSessionService {

	private final Map<String, SessionInfo> usernameToSession = new ConcurrentHashMap<>();
	
	// Grace period for token validation
	private static final long TOKEN_GRACE_PERIOD_SECONDS = 10;

	public SessionInfo replaceActiveSession(String username, String newAccessTokenId, String newRefreshTokenId,
			Instant accessExpiresAt, Instant refreshExpiresAt) {
		if (username == null) {
			return null;
		}
		SessionInfo previous = usernameToSession.put(username, new SessionInfo(newAccessTokenId, newRefreshTokenId, accessExpiresAt, refreshExpiresAt, Instant.now()));
		log.debug("Active session set for user: {} (access jti: {}, refresh jti: {})", username, shortId(newAccessTokenId), shortId(newRefreshTokenId));
		return previous;
	}

	/**
	 * Check if token is current with grace period for concurrent requests
	 */
	public boolean isCurrentAccessToken(String username, String tokenId) {
		SessionInfo info = usernameToSession.get(username);
		if (info == null || tokenId == null) {
			return false;
		}
		
		if (tokenId.equals(info.getAccessTokenId())) {
			return true;
		}
		
		Instant gracePeriodStart = info.getUpdatedAt().minusSeconds(TOKEN_GRACE_PERIOD_SECONDS);
		Instant now = Instant.now();
		
		if (now.isAfter(gracePeriodStart) && now.isBefore(info.getUpdatedAt().plusSeconds(TOKEN_GRACE_PERIOD_SECONDS))) {
			log.debug("Allowing token within grace period for user: {} (token: {})", username, shortId(tokenId));
			return true;
		}
		
		return false;
	}

	public boolean isCurrentRefreshToken(String username, String tokenId) {
		SessionInfo info = usernameToSession.get(username);
		return info != null && tokenId != null && tokenId.equals(info.getRefreshTokenId());
	}

	@Scheduled(fixedRate = 3600000)
	public void cleanupExpiredSessions() {
		Instant now = Instant.now();
		int beforeSize = usernameToSession.size();
		usernameToSession.entrySet().removeIf(e -> {
			SessionInfo s = e.getValue();
			return (s.getRefreshExpiresAt() != null && s.getRefreshExpiresAt().isBefore(now))
				|| (s.getAccessExpiresAt() != null && s.getAccessExpiresAt().isBefore(now));
		});
		int afterSize = usernameToSession.size();
		if (beforeSize != afterSize) {
			log.debug("Cleaned up {} expired sessions", beforeSize - afterSize);
		}
	}

	private String shortId(String id) {
		if (id == null || id.length() < 6) return "***";
		return id.substring(0, 3) + "..." + id.substring(id.length() - 3);
	}

	@Data
	public static class SessionInfo {
		private final String accessTokenId;
		private final String refreshTokenId;
		private final Instant accessExpiresAt;
		private final Instant refreshExpiresAt;
		private final Instant updatedAt;
	}
}

