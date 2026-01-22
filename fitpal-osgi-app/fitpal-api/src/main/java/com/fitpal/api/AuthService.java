package com.fitpal.api;

public interface AuthService {
    String login(String email, String password, Boolean reactivate) throws Exception;
}