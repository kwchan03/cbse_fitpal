package com.fitpal.fitpalspringbootapp.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse {
    private Integer mealId;
    private String foodName;
    private String imageUrl;
    private Integer calories;
    private Integer protein;
    private Integer fat;
    private Integer carbs;
    private String mealType;
}