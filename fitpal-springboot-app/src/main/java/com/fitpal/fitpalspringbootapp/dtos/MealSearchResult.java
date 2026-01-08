package com.fitpal.fitpalspringbootapp.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealSearchResult {
    private Integer mealId;
    private String foodName;
    private String imageUrl;
}