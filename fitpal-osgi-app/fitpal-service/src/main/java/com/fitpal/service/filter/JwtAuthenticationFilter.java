package com.fitpal.service.filter;

import com.fitpal.service.auth.TokenService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Provider
@Component(service = ContainerRequestFilter.class, property = {
        "osgi.jaxrs.extension=true",
        "osgi.jaxrs.name=jwtAuthenticationFilter"
})
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    @Reference
    private TokenService tokenService;

    // Define paths with leading slashes
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/logout",
            "/api/users/register",
            "/api/users/reactivate"
    );

    @Override
    public void filter(ContainerRequestContext context) throws IOException {

        // 1. CORS Preflight
        if ("OPTIONS".equalsIgnoreCase(context.getMethod())) {
            return;
        }

        // --- DEBUGGING START ---
        String rawPath = context.getUriInfo().getPath();
        // Normalize: Ensure it starts with "/"
        String path = rawPath.startsWith("/") ? rawPath : "/" + rawPath;

        System.out.println("[JwtFilter] Incoming Path: " + rawPath);
        System.out.println("[JwtFilter] Normalized Path: " + path);
        // --- DEBUGGING END ---

        // 2. Public Endpoint Check
        // Now "path" definitely has a slash, so it will match your list
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::endsWith)) {
            System.out.println("[JwtFilter] Public Endpoint Matched - Allowing");
            return;
        }

        // 3. Get Cookie
        Cookie cookie = context.getCookies().get("auth_token");
        String token = (cookie != null) ? cookie.getValue() : null;

        // 4. Validate Token
        if (token == null || !tokenService.validateToken(token)) {
            System.out.println("[JwtFilter] Blocking request to: " + path);
            abortUnauthorized(context);
            return;
        }

        // 5. Extract User ID
        String userId = tokenService.getUserIdFromToken(token);
        if (userId != null) {
            context.setProperty("userId", userId);
            System.out.println("[JwtFilter] User ID set: " + userId);
        }
    }

    private void abortUnauthorized(ContainerRequestContext context) {
        context.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"message\":\"Unauthorized\"}")
                .type("application/json")
                .build());
    }
}