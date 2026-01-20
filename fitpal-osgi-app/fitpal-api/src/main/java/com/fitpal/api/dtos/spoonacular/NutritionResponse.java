package com.fitpal.api.dtos.spoonacular;

import java.util.List;

public class NutritionResponse {
    private List<Nutrient> nutrients;

    public NutritionResponse() {
    }

    public List<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    // Nested Nutrient class
    public static class Nutrient {
        private String name;
        private Double amount;
        private String unit;

        public Nutrient() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}