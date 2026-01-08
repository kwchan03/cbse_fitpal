package com.fitpal.fitpalspringbootapp.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFoodRequest {
    private String date;
    private String type; // breakfast, lunch, dinner
    private Integer mealId;
    private String foodName;
}