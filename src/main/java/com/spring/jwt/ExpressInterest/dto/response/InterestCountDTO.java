package com.spring.jwt.ExpressInterest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterestCountDTO {

    private Long sentAccepted;
    private Long sentDeclined;
    private Long sentPending;

    private Long receivedAccepted;
    private Long receivedDeclined;
    private Long receivedPending;
}
