package com.fitpal.fitpalspringbootapp.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendMealRequest {
    private Integer targetCalories;
    private String date; // ISO date format: yyyy-MM-dd
}