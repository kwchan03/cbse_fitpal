package com.fitpal.fitpalspringbootapp.dtos.spoonacular;

import lombok.Data;
import java.util.List;

@Data
public class NutritionResponse {
    private List<Nutrient> nutrients;

    @Data
    public static class Nutrient {
        private String name;
        private Double amount;
        private String unit;
    }
}