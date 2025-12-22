package com.spring.jwt.ExpressInterest.dto.response;

import com.spring.jwt.entity.Enums.InterestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressInterestDTO {

    private Long interestId;

    private Integer fromUser;
    private Integer toUserId;

    private InterestStatus status;
    private LocalDateTime createdAt;
}
