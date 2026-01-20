package com.fitpal.fitpalspringbootapp.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ==================== CREATE PHYSICAL INFO REQUEST ====================
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePhysicalInfoRequest {

    @NotNull(message = "Weight is required")
    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight cannot exceed 500 kg")
    private Double weight; // in kilograms

    @NotNull(message = "Height is required")
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height cannot exceed 300 cm")
    private Double height; // in cm

    @NotNull(message = "Activity level is required")
    @Min(value = 1, message = "Activity level must be between 1 and 5")
    @Max(value = 5, message = "Activity level must be between 1 and 5")
    private Double activityLevel;

    @NotNull(message = "Weight goal is required")
    @Pattern(regexp = "^(-500|0|500)$", message = "Weight goal must be -500, 0, or 500")
    private String weightGoal; // Using String for validation, convert in service
}
