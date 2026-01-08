package com.fitpal.fitpalspringbootapp.dtos.spoonacular;

import lombok.Data;

@Data
public class RecipeAutocomplete {
    private Integer id;
    private String title;
    private String imageType;
}