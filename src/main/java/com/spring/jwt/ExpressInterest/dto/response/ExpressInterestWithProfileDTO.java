package com.spring.jwt.ExpressInterest.dto.response;

import com.spring.jwt.profile.dto.response.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressInterestWithProfileDTO {

    private Long interestId;

    private Integer fromUserId;
    private Integer toUserId;

    private String status;
    private String createdAt;

    private ProfileResponse senderProfile;

    public ExpressInterestWithProfileDTO(Long interestId, int intExact, Integer toUserId, String name, String string,
            ProfileResponse senderDto) {
        this.interestId = interestId;
        this.fromUserId = intExact;
        this.toUserId = toUserId;
        this.status = name;
        this.createdAt = string;
        this.senderProfile = senderDto;
    }
}
