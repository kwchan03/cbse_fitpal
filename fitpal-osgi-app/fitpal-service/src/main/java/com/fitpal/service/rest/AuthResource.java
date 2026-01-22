package com.fitpal.service.rest;

import com.fitpal.api.AuthService;
import com.fitpal.api.User;
import com.fitpal.api.dtos.LoginRequest;
import com.fitpal.api.dtos.LoginResponse;
import com.fitpal.service.auth.TokenService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component(service = AuthResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/auth")
public class AuthResource {

    @Reference
    private AuthService authService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            Boolean reactivate = loginRequest.getReactivate();

            if (email == null || password == null) {
                return Response.status(400)
                        .entity(Map.of("message", "email and password required"))
                        .build();
            }

            String token = authService.login(email, password, reactivate);

            LoginResponse response = new LoginResponse(
                    "Login successful",
                    null,
                    loginRequest.getEmail()
            );

            NewCookie cookie = new NewCookie("auth_token", token, "/", null, null, 86400, true, false);
            return Response.ok(response).cookie(cookie).build();
        } catch (RuntimeException e) {
            return Response.status(401).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"message\": \"Login failed\"}").build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        NewCookie cookie = new NewCookie("auth_token", "", "/", null, null, 0, true, false);
        return Response.ok("{\"message\": \"Logged out\"}").cookie(cookie).build();
    }
}