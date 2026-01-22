package com.fitpal.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("email")
    private String email;

    // Constructors
    public LoginResponse() {
    }

    public LoginResponse(String message, String userId, String email) {
        this.message = message;
        this.userId = userId;
        this.email = email;
    }

    // Getters & Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
