package com.spring.jwt.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 5: Family Background Request
 * Contains family background information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyBackgroundStepRequest {

    @NotBlank(message = "Father's name cannot be blank")
    @Size(max = 100, message = "Father's name cannot exceed 100 characters")
    private String fathersName;

    @NotBlank(message = "Mother's name cannot be blank")
    @Size(max = 100, message = "Mother's name cannot exceed 100 characters")
    private String mothersName;

    @NotBlank(message = "Father's occupation cannot be blank")
    @Size(max = 100, message = "Father's occupation cannot exceed 100 characters")
    private String fatherOccupation;

    @NotBlank(message = "Mother's occupation cannot be blank")
    @Size(max = 100, message = "Mother's occupation cannot exceed 100 characters")
    private String motherOccupation;

    @NotNull(message = "Number of brothers cannot be null")
    @Min(value = 0, message = "Number of brothers cannot be negative")
    private Integer brother;

    @NotNull(message = "Number of sisters cannot be null")
    @Min(value = 0, message = "Number of sisters cannot be negative")
    private Integer sisters;

    @NotNull(message = "Number of married brothers cannot be null")
    @Min(value = 0, message = "Number of married brothers cannot be negative")
    private Integer marriedBrothers;

    @NotNull(message = "Number of married sisters cannot be null")
    @Min(value = 0, message = "Number of married sisters cannot be negative")
    private Integer marriedSisters;

    @NotBlank(message = "Mama surname cannot be blank")
    @Size(max = 100, message = "Mama surname cannot exceed 100 characters")
    private String mamaSurname;

    @NotBlank(message = "Mama place cannot be blank")
    @Size(max = 100, message = "Mama place cannot exceed 100 characters")
    private String mamaPlace;

    @NotBlank(message = "Parent residing information cannot be blank")
    @Size(max = 200, message = "Parent residing cannot exceed 200 characters")
    private String parentResiding;

    @NotNull(message = "Inter-caste information cannot be null")
    private Boolean interCasteInFamily;
}