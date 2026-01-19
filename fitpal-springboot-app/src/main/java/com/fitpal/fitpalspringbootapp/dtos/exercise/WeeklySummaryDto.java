package com.fitpal.fitpalspringbootapp.dtos.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklySummaryDto {
    private Integer averageSteps;
    private Integer averageMinutes;
    private Integer averageCalories;
}
