package com.spring.jwt.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Full profile response DTO with all fields and audit metadata.
 * Used for authenticated users viewing profiles they have access to.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Integer userProfileId;
    private Integer version;

    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private String gender;
    private String status;

    private String address;
    private String taluka;
    private String district;
    private Integer pinCode;

    private String religion;
    private String caste;
    private String maritalStatus;
    private Double height;
    private Integer weight;
    private String bloodGroup;
    private String complexion;
    private String diet;

    private Boolean spectacle;
    private Boolean lens;
    private Boolean physicallyChallenged;

    private String homeTownDistrict;
    private String nativeTaluka;
    private String currentCity;

    private Integer userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
}
