package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.*;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.services.UserService;
import com.fitpal.fitpalspringbootapp.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user
     * TODO: To be completed
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        try {
            // TODO: Add validation
            User savedUser = userService.registerUser(registerRequest);

            // Generate JWT token
            String token = jwtUtil.generateToken(savedUser.getId());

            // Set cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 1 day
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Create/Update user profile
     * TODO: To be completed
     */
    @PutMapping("/create-profile")
    public ResponseEntity<?> createProfile(
            @RequestAttribute("userId") String userId,
            @Valid @ModelAttribute("user") CreateProfileRequest profileRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            // TODO: Add validation
            User updatedUser = userService.registerUserProfile(userId, profileRequest, imageFile);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Create/Update user physical information
     * TODO: To be completed
     */
    @PutMapping("/create-physical")
    public ResponseEntity<?> createPhysicalInfo(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody CreatePhysicalInfoRequest physicalRequest) {
        try {
            // TODO: Add validation
            User updatedUser = userService.registerUserPhysicalInfo(userId, physicalRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get user profile information
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestAttribute("userId") String userId) {
        try {
            User user = userService.getUserInfo(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Update user profile
     * TODO: To be completed
     */
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @RequestAttribute("userId") String userId,
            @Valid @ModelAttribute("user") UpdateProfileRequest profileRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            // TODO: Add validation
            User updatedUser = userService.updateUserProfile(userId, profileRequest, imageFile);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Change user password
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody ChangePasswordRequest passwordRequest) {
        try {
            userService.changePassword(userId, passwordRequest.getCurrentPassword(),
                    passwordRequest.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect current password")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Deactivate user account
     */
    @PutMapping("/deactivate-account")
    public ResponseEntity<?> deactivateAccount(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody DeactivateAccountRequest deactivateRequest,
            HttpServletResponse response) {
        try {
            userService.deactivateAccount(userId, deactivateRequest.getPassword());

            // Clear auth cookie
            Cookie cookie = new Cookie("auth_token", "");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect password")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Reactivate user account
     */
    @PutMapping("/reactivate")
    public ResponseEntity<?> reactivateAccount(
            @Valid @RequestBody ReactivateAccountRequest reactivateRequest,
            HttpServletResponse response) {
        try {
            String token = userService.reactivateAccount(
                    reactivateRequest.getEmail(),
                    reactivateRequest.getPassword()
            );

            // Set auth cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 1 day
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of("message", "Account reactivated and signed in"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect password")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", e.getMessage()));
            } else if (e.getMessage().equals("Account is already active")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Delete user account permanently
     */
    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(
            @RequestAttribute("userId") String userId,
            @RequestBody Map<String, String> body,
            HttpServletResponse response) {
        try {
            String password = body.get("password");
            userService.deleteAccount(userId, password);

            // Clear auth cookie
            Cookie cookie = new Cookie("auth_token", "");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect password")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    /**
     * Get user daily goals
     */
    @GetMapping("/goals")
    public ResponseEntity<?> getUserGoals(@RequestAttribute("userId") String userId) {
        try {
            Map<String, Object> goals = userService.getUserGoals(userId);
            return ResponseEntity.ok(goals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error"));
        }
    }
}