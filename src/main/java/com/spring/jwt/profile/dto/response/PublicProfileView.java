package com.spring.jwt.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public profile view with limited information.
 * Used for unauthenticated users or listing views.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicProfileView {

    private Integer userProfileId;
    private String firstName;
    private Integer age;
    private String gender;
    private String religion;
    private String caste;
    private Double height;
    private String complexion;
    private String currentCity;
    private String maritalStatus;
}
