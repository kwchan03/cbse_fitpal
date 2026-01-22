package com.fitpal.service.rest;

import com.fitpal.api.User;
import com.fitpal.api.UserService;
import com.fitpal.api.dtos.*;
import com.fitpal.service.auth.TokenService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component(service = UserResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/users")
public class UserResource {

    @Reference
    private UserService userService;

    @Reference
    private TokenService tokenService;

    // --- Cookie Helpers (Same as AuthResource) ---
    private NewCookie createAuthCookie(String token) {
        return new NewCookie(
                "auth_token", token, "/", null, null,
                86400, true, false // Secure: false for dev, true for prod
        );
    }

    private NewCookie clearAuthCookie() {
        return new NewCookie(
                "auth_token", "", "/", null, null,
                0, true, false
        );
    }

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    // ========== Routes ==========

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null)
                return errorResponse(400, "Email and password are required");

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());

            User savedUser = userService.registerUser(user);

            // AUTO-LOGIN: Generate Token & Cookie
            String token = tokenService.generateToken(savedUser.getId());
            NewCookie cookie = createAuthCookie(token);

            // Note: Spring Boot returns "ApiResponse.success", adjusting to map for consistency
            return Response.status(Response.Status.CREATED)
                    .entity(savedUser)
                    .cookie(cookie)
                    .build();
        } catch (RuntimeException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(400, "Registration failed: " + e.getMessage());
        }
    }

    @PUT
    @Path("/create-profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProfile(@QueryParam("userId") String userId, CreateProfileRequest request) {
        try {
            if (userId == null || userId.isEmpty()) return errorResponse(400, "userId is required");

            User profileData = new User();
            profileData.setFirstName(request.getFirstName());
            profileData.setLastName(request.getLastName());
            profileData.setGender(request.getGender());
            profileData.setDob(request.getDob());
            // Handle DOB conversion from String if necessary, or assume proper format

            // Image handling (Base64)
            String imageBase64 = request.getImageFile();
            byte[] imageBytes = null;
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                imageBytes = java.util.Base64.getDecoder().decode(imageBase64);
            }

            User updatedUser = userService.registerUserProfile(userId, profileData, imageBytes);
            return successResponse(200, updatedUser);
        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Profile creation failed: " + e.getMessage());
        }
    }

    @PUT
    @Path("/create-physical")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPhysicalInfo(@QueryParam("userId") String userId, CreatePhysicalInfoRequest request) {
        try {
            if (userId == null || userId.isEmpty()) return errorResponse(400, "userId is required");

            User physicalData = new User();
            physicalData.setWeight(request.getWeight());
            physicalData.setHeight(request.getHeight());
            physicalData.setActivityLevel(request.getActivityLevel());
            physicalData.setWeightGoal(request.getWeightGoal());

            return successResponse(200, physicalData);
        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Physical info creation failed: " + e.getMessage());
        }
    }

    @GET
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@QueryParam("userId") String userId) {
        try {
            if (userId == null || userId.isEmpty()) return errorResponse(400, "userId is required");

            User user = userService.getUserInfo(userId);
            return successResponse(200, user);
        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving profile: " + e.getMessage());
        }
    }

    @PUT
    @Path("/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(@QueryParam("userId") String userId, UpdateProfileRequest request) {
        try {
            if (userId == null || userId.isEmpty()) return errorResponse(400, "userId is required");

            User profileData = new User();
            if (request.getFirstName() != null) profileData.setFirstName(request.getFirstName());
            if (request.getLastName() != null) profileData.setLastName(request.getLastName());
            if (request.getGender() != null) profileData.setGender(request.getGender());
            if (request.getDob() != null) profileData.setDob(request.getDob());
            if (request.getWeight() != null) profileData.setWeight(request.getWeight());
            if (request.getHeight() != null) profileData.setHeight(request.getHeight());
            if (request.getActivityLevel() != null) profileData.setActivityLevel(request.getActivityLevel());
            if (request.getWeightGoal() != null) profileData.setWeightGoal(request.getWeightGoal());

            String imageBase64 = request.getImageFile();
            byte[] imageBytes = null;
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                imageBytes = java.util.Base64.getDecoder().decode(imageBase64);
            }
            User updatedUser = userService.updateUserProfile(userId, profileData, imageBytes);
            return successResponse(200, updatedUser);
        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Profile update failed: " + e.getMessage());
        }
    }

    @PUT
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@QueryParam("userId") String userId, ChangePasswordRequest request) {
        try {
            if (userId == null || userId.isEmpty()) return errorResponse(400, "userId is required");

            String currentPass = request.getCurrentPassword();
            String newPass = request.getNewPassword();
            String confirmPass = request.getConfirmPassword();

            if (currentPass == null || newPass == null) return errorResponse(400, "Passwords required");
            if (confirmPass != null && !newPass.equals(confirmPass)) return errorResponse(400, "New passwords do not match");

            userService.changePassword(userId, currentPass, newPass);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password updated successfully");
            return successResponse(200, response);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect current password")) return errorResponse(401, e.getMessage());
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Password change failed: " + e.getMessage());
        }
    }

    @PUT
    @Path("/deactivate-account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateAccount(@QueryParam("userId") String userId, PasswordVerificationRequest request) {
        try {
            if (userId == null || userId.isEmpty()) return errorResponse(400, "userId is required");

            String password = request.getPassword();
            if (password == null) return errorResponse(400, "password is required");

            userService.deactivateAccount(userId, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Account deactivated successfully");

            // Clear Cookie on Deactivation
            return Response.ok(response).cookie(clearAuthCookie()).build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect password")) return errorResponse(401, e.getMessage());
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Deactivation failed: " + e.getMessage());
        }
    }

    @PUT
    @Path("/reactivate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reactivateAccount(ReactivateAccountRequest request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();

            if (email == null || password == null) {
                return errorResponse(400, "email and password are required");
            }

            // GET TOKEN DIRECTLY FROM SERVICE
            String token = userService.reactivateAccount(email, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Account reactivated and signed in");

            return Response.ok(response).cookie(createAuthCookie(token)).build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect password")) {
                return errorResponse(401, e.getMessage());
            }
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Reactivation failed: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/delete-account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@QueryParam("userId") String userId, PasswordVerificationRequest request) {
        try {
            if (userId == null || userId.isEmpty()) return errorResponse(400, "userId is required");

            String password = request.getPassword();
            if (password == null) return errorResponse(400, "password is required");

            userService.deleteAccount(userId, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Account deleted successfully");

            // Clear Cookie on Deletion
            return Response.ok(response).cookie(clearAuthCookie()).build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Incorrect password")) return errorResponse(401, e.getMessage());
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Account deletion failed: " + e.getMessage());
        }
    }

    @GET
    @Path("/goals")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserGoals(@QueryParam("userId") String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            Map<String, Object> goals = userService.getUserGoals(userId);
            if (goals == null || goals.isEmpty()) {
                return errorResponse(404, "User not found or no goals set");
            }
            return successResponse(200, goals);
        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving goals: " + e.getMessage());
        }
    }
}