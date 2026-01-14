package com.spring.jwt.admin.dto;

import com.spring.jwt.entity.Enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserListResponse {
    private Integer userId;
    private String profileId;
    private String firstName;
    private String lastName;
    private Integer age;
    private String city;
    private Gender gender;
    private String religion;
    private String caste;
    private String profession;
    private String membership; // Basic, Premium, Elite, Platinum
    private String verification; // Verified or Non-Verified
    private Integer sendRequests;
    private Integer receiveRequests;
    private String status; // Active or Deactivate
    private String email;
    private Long mobileNumber;
}
