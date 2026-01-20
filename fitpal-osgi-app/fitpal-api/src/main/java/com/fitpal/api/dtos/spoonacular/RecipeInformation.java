package com.fitpal.api.dtos.spoonacular;

import java.util.List;

public class RecipeInformation {
    private Integer id;
    private String title;
    private String image;
    private Integer servings;
    private Integer readyInMinutes;
    private String sourceUrl;
    private String summary;
    private List<String> dishTypes;

    public RecipeInformation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(List<String> dishTypes) {
        this.dishTypes = dishTypes;
    }
}