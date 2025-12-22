package com.spring.jwt.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Step 3: Horoscope Details Request
 * Contains horoscope and astrological information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoroscopeDetailsStepRequest {

    @NotNull(message = "Date of Birth cannot be null")
    private LocalDate dob;

    @NotNull(message = "Time of Birth cannot be empty")
    private LocalTime time;

    @NotBlank(message = "Birth Place cannot be empty")
    @Size(max = 100, message = "Birth place cannot exceed 100 characters")
    private String birthPlace;

    @NotBlank(message = "Nakshatra cannot be empty")
    @Size(max = 50, message = "Nakshatra cannot exceed 50 characters")
    private String nakshatra;

    @NotBlank(message = "Rashi cannot be empty")
    @Size(max = 50, message = "Rashi cannot exceed 50 characters")
    private String rashi;

    @NotBlank(message = "Charan cannot be empty")
    @Pattern(regexp = "^[1-4]$", message = "Charan must be between 1 and 4")
    private String charan;

    @NotBlank(message = "Nadi cannot be empty")
    @Size(max = 50, message = "Nadi cannot exceed 50 characters")
    private String nadi;

    @NotBlank(message = "Devak cannot be empty")
    @Size(max = 100, message = "Devak cannot exceed 100 characters")
    private String devak;

    @NotBlank(message = "Gan cannot be empty")
    @Size(max = 50, message = "Gan cannot exceed 50 characters")
    private String gan;

    @NotBlank(message = "Gotra cannot be empty")
    @Size(max = 100, message = "Gotra cannot exceed 100 characters")
    private String gotra;

    @NotBlank(message = "Mangal cannot be empty")
    @Pattern(regexp = "^(Yes|No)$", message = "Mangal must be Yes or No")
    private String mangal;
}