package com.fitpal.service.rest;

import com.fitpal.api.User;
import com.fitpal.api.UserService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component(service = UserResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/users")
public class UserResource {

    @Reference
    private UserService userService;

    // Removed TokenService reference

    // Removed createAuthCookie / clearAuthCookie helpers

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    // ========== Routes (Auth stripped) ==========

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        try {
            User savedUser = userService.registerUser(user);
            // Return user object WITHOUT cookie/token
            return Response.status(201).entity(savedUser).build();
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
    public Response createProfile(@QueryParam("userId") String userId, Map<String, Object> body) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            User updates = new User();
            updates.setFirstName((String) body.get("firstName"));
            updates.setLastName((String) body.get("lastName"));

            String imageBase64 = (String) body.get("imageFile");
            byte[] imageBytes = null;
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    imageBytes = java.util.Base64.getDecoder().decode(imageBase64);
                } catch (IllegalArgumentException e) {
                    return errorResponse(400, "Invalid Base64 image format");
                }
            }

            User updatedUser = userService.registerUserProfile(userId, updates, imageBytes);
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
    public Response createPhysicalInfo(@QueryParam("userId") String userId, User user) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            User updatedUser = userService.registerUserPhysicalInfo(userId, user);
            return successResponse(200, updatedUser);
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
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            User user = userService.getUserInfo(userId);
            if (user == null) {
                return errorResponse(404, "User does not exist");
            }
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
    public Response updateProfile(@QueryParam("userId") String userId, Map<String, Object> body) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            User profileData = new User();
            profileData.setFirstName((String) body.get("firstName"));
            profileData.setLastName((String) body.get("lastName"));

            String imageBase64 = (String) body.get("imageFile");
            byte[] imageBytes = null;
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    imageBytes = java.util.Base64.getDecoder().decode(imageBase64);
                } catch (IllegalArgumentException e) {
                    return errorResponse(400, "Invalid Base64 image format");
                }
            }

            User updatedUser = userService.registerUserProfile(userId, profileData, imageBytes);
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
    public Response changePassword(@QueryParam("userId") String userId, Map<String, String> body) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            String currentPass = body.get("currentPassword");
            String newPass = body.get("newPassword");

            if (currentPass == null || newPass == null) {
                return errorResponse(400, "currentPassword and newPassword are required");
            }

            userService.changePassword(userId, currentPass, newPass);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password updated successfully");
            return successResponse(200, response);
        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Password change failed: " + e.getMessage());
        }
    }

    @PUT
    @Path("/deactivate-account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateAccount(@QueryParam("userId") String userId, Map<String, String> body) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            String password = body.get("password");
            // Password checked for existence but not verification
            if (password == null) {
                return errorResponse(400, "password is required");
            }

            userService.deactivateAccount(userId, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Account deactivated successfully");
            return Response.ok(response).build(); // Removed cookie clearing
        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Deactivation failed: " + e.getMessage());
        }
    }

    @PUT
    @Path("/deactivate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateAlias(@QueryParam("userId") String userId, Map<String, String> body) {
        return deactivateAccount(userId, body);
    }

    @PUT
    @Path("/reactivate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reactivateAccount(Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            if (email == null || password == null) {
                return errorResponse(400, "email and password are required");
            }

            userService.reactivateAccount(email, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Account reactivated");
            return Response.ok(response).build(); // Removed cookie setting
        } catch (RuntimeException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Reactivation failed: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/delete-account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@QueryParam("userId") String userId, Map<String, String> body) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            String password = body.get("password");
            if (password == null) {
                return errorResponse(400, "password is required");
            }

            userService.deleteAccount(userId, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Account deleted successfully");
            return Response.ok(response).build(); // Removed cookie clearing
        } catch (RuntimeException e) {
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