package com.spring.jwt.dto.horoscope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Response DTO for horoscope details with all fields and audit metadata.
 * Used for authenticated users viewing their horoscope details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoroscopeResponse {
    private Integer horoscopeDetailsId;
    private Integer version;
    
    private Date dob;
    private String time;
    private String birthPlace;
    private String rashi;
    private String nakshatra;
    private String charan;
    private String nadi;
    private String gan;
    private String mangal;
    private String gotra;
    private String devak;

    private Integer userId;

    private Instant createdAt;
    private Instant updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
}
