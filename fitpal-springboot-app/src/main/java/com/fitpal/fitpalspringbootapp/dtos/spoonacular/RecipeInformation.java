package com.fitpal.fitpalspringbootapp.dtos.spoonacular;

import lombok.Data;
import java.util.List;

@Data
public class RecipeInformation {
    private Integer id;
    private String title;
    private String image;
    private Integer servings;
    private Integer readyInMinutes;
    private String sourceUrl;
    private String summary;
    private List<String> dishTypes;
}