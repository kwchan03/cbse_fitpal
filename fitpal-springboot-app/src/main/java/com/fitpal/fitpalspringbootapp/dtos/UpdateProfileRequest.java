package com.fitpal.fitpalspringbootapp.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// ==================== UPDATE PROFILE REQUEST ====================
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Pattern(regexp = "^(Male|Female)$", message = "Gender must be Male or Female")
    private String gender;

    @PastOrPresent(message = "Date of birth cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight cannot exceed 500 kg")
    private Double weight; // in kilograms

    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height cannot exceed 300 cm")
    private Double height; // in cm

    @DecimalMin(value = "1.0", message = "Activity level must be at least 1.0")
    @DecimalMax(value = "2.0", message = "Activity level must be at most 2.0")
    private Double activityLevel;

    @Pattern(regexp = "^(-500|0|500)$", message = "Weight goal must be -500, 0, or 500")
    private String weightGoal;
}
