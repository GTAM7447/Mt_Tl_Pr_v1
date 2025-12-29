package com.spring.jwt.dto.horoscope;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * Request DTO for updating horoscope details.
 * All fields are optional for partial updates (PATCH semantics).
 * Includes version for optimistic locking.
 */
@Data
public class HoroscopeUpdateRequest {

    @NotNull(message = "Version is required for updates to prevent conflicts")
    private Integer version;

    @Past(message = "Date of Birth must be in the past")
    private Date dob;

    @Size(max = 45, message = "Time must be less than 45 characters")
    private String time;

    @Size(max = 45, message = "Birth Place must be less than 45 characters")
    private String birthPlace;

    @Size(max = 45, message = "Rashi must be less than 45 characters")
    private String rashi;

    @Size(max = 45, message = "Nakshatra must be less than 45 characters")
    private String nakshatra;

    @Size(max = 45, message = "Charan must be less than 45 characters")
    private String charan;

    @Size(max = 45, message = "Nadi must be less than 45 characters")
    private String nadi;

    @Size(max = 45, message = "Gan must be less than 45 characters")
    private String gan;

    @Size(max = 45, message = "Mangal must be less than 45 characters")
    private String mangal;

    @Size(max = 45, message = "Gotra must be less than 45 characters")
    private String gotra;

    @Size(max = 45, message = "Devak must be less than 45 characters")
    private String devak;
}
