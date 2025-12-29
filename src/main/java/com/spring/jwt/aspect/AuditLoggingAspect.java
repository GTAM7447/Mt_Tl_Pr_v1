package com.spring.jwt.aspect;

import com.spring.jwt.service.AsyncAuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingAspect {

    private final AsyncAuditService auditService;

    @Pointcut("@annotation(com.spring.jwt.aspect.Loggable)")
    public void loggableMethods() {
    }

    @Around("loggableMethods()")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        String status = "SUCCESS";
        String details = "";

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            status = "FAILURE";
            details = "Error: " + e.getMessage();
            throw e;
        } finally {
            try {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Loggable annotation = signature.getMethod().getAnnotation(Loggable.class);
                String action = annotation.action();

                if (action.isEmpty()) {
                    action = signature.getMethod().getName();
                }

                String userId = getCurrentUserId();
                String ipAddress = getClientIp();

                auditService.logActivity(userId, action, details, ipAddress, status);
            } catch (Exception e) {
                log.error("Error in audit aspect", e);
            }
        }
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "ANONYMOUS";
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e)
        {
        }
        return "UNKNOWN";
    }
}
