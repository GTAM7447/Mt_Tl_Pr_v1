package com.spring.jwt.Subscription;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

    private Integer subscriptionId;

    @NotBlank(message = "Subscription name cannot be blank")
    @Size(max = 45)
    private String name;

    @NotNull(message = "Credit must not be null")
    @Min(value = 1, message = "Credit must be at least 1")
    private Integer credit;

    @NotNull(message = "Created date cannot be null")
    private LocalDate createdDate;

    @NotNull(message = "Day limit cannot be null")
    @Min(value = 1, message = "Day limit must be at least 1")
    private Integer dayLimit;

    @NotNull(message = "Time period (months) cannot be null")
    @Min(value = 1, message = "Subscription should last at least 1 month")
    private Integer timePeriodMonths;

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status;
}
