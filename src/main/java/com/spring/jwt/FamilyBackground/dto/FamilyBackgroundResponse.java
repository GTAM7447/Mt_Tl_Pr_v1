package com.spring.jwt.FamilyBackground.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for family background with all fields and audit metadata.
 * Used for authenticated users viewing their family background details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyBackgroundResponse {
    
    private Integer familyBackgroundId;
    private Integer version;
    private String fathersName;
    private String fatherOccupation;
    private String mothersName;
    private String motherOccupation;
    private Integer brother;
    private Integer marriedBrothers;
    private Integer sisters;
    private Integer marriedSisters;
    private Boolean interCasteInFamily;
    private String parentResiding;
    private String familyWealth;
    private String mamaSurname;
    private String mamaPlace;
    private String familyBackgroundCol;
    private String relativeSurnames;

    private Integer userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
}