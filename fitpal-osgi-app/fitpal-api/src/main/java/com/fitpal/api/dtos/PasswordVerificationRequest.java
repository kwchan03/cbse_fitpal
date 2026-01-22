package com.fitpal.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordVerificationRequest {

    @JsonProperty("password")
    private String password;

    // Constructors
    public PasswordVerificationRequest() {
    }

    public PasswordVerificationRequest(String password) {
        this.password = password;
    }

    // Getters & Setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
