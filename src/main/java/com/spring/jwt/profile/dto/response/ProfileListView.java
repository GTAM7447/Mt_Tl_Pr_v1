package com.spring.jwt.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight profile view for list/browse operations.
 * Contains summary information to reduce payload size.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileListView {

    private Integer userProfileId;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private String religion;
    private String caste;
    private Double height;
    private String district;
    private String currentCity;
    private String maritalStatus;
    private String status;
}
