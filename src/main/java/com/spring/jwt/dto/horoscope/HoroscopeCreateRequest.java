package com.spring.jwt.dto.horoscope;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class HoroscopeCreateRequest {

    @NotNull(message = "Date of Birth cannot be null")
    @Past(message = "Date of Birth must be in the past")
    private Date dob;

    @NotBlank(message = "Time of Birth cannot be empty")
    @Size(max = 45, message = "Time must be less than 45 characters")
    private String time;

    @NotBlank(message = "Birth Place cannot be empty")
    @Size(max = 45, message = "Birth Place must be less than 45 characters")
    private String birthPlace;

    @NotBlank(message = "Rashi cannot be empty")
    @Size(max = 45, message = "Rashi must be less than 45 characters")
    private String rashi;

    @NotBlank(message = "Nakshatra cannot be empty")
    @Size(max = 45, message = "Nakshatra must be less than 45 characters")
    private String nakshatra;

    @NotBlank(message = "Charan cannot be empty")
    @Size(max = 45, message = "Charan must be less than 45 characters")
    private String charan;

    @NotBlank(message = "Nadi cannot be empty")
    @Size(max = 45, message = "Nadi must be less than 45 characters")
    private String nadi;

    @NotBlank(message = "Gan cannot be empty")
    @Size(max = 45, message = "Gan must be less than 45 characters")
    private String gan;

    @NotBlank(message = "Mangal cannot be empty")
    @Size(max = 45, message = "Mangal must be less than 45 characters")
    private String mangal;

    @NotBlank(message = "Gotra cannot be empty")
    @Size(max = 45, message = "Gotra must be less than 45 characters")
    private String gotra;

    @NotBlank(message = "Devak cannot be empty")
    @Size(max = 45, message = "Devak must be less than 45 characters")
    private String devak;
}
