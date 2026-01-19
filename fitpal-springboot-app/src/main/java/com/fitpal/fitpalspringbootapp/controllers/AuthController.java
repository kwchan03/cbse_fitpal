package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.LoginRequest;
import com.fitpal.fitpalspringbootapp.dtos.UserIdResponse;
import com.fitpal.fitpalspringbootapp.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Login endpoint - authenticates user and returns JWT token in cookie
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        try {
            // TODO: Add validation for login request
            String token = authService.login(
                    loginRequest.getEmail(),
                    loginRequest.getPassword(),
                    loginRequest.getReactivate()
            );

            // Set JWT token in HTTP-only cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 1 day in seconds
            cookie.setPath("/");
            // cookie.setSecure(true); // Enable in production with HTTPS
            response.addCookie(cookie);

            // Extract userId from token and return
            // Note: In your Node.js version, you return the userId
            // You can either decode the token here or get it from the service
            return ResponseEntity.ok(Map.of("message", "Login successful"));

        } catch (RuntimeException e) {
            String message = e.getMessage();

            if ("Invalid Credentials".equals(message)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", message));
            } else if (message.contains("deactivated")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", message));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", message));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Validate token endpoint - returns the authenticated user's ID
     * This endpoint is protected by JwtAuthenticationFilter
     */
    @GetMapping("/validate-token")
    public ResponseEntity<UserIdResponse> validateToken(@RequestAttribute("userId") String userId) {
        return ResponseEntity.ok(new UserIdResponse(userId));
    }

    /**
     * Logout endpoint - clears the auth token cookie
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Clear the auth_token cookie
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expire immediately
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}