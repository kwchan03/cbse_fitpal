package com.fitpal.fitpalspringbootapp.dtos;

public class BadgeResponse {

    private String name;

    private String description;

    public BadgeResponse() {}

    public BadgeResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}