package com.fitpal.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReactivateAccountRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    // Constructors
    public ReactivateAccountRequest() {
    }

    public ReactivateAccountRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters & Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
