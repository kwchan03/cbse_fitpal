// ============================================================
// LOGIN & AUTH DTOs
// ============================================================

package com.fitpal.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("reactivate")
    private Boolean reactivate = false;

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String email, String password, Boolean reactivate) {
        this.email = email;
        this.password = password;
        this.reactivate = reactivate;
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

    public Boolean getReactivate() {
        return reactivate;
    }

    public void setReactivate(Boolean reactivate) {
        this.reactivate = reactivate;
    }
}