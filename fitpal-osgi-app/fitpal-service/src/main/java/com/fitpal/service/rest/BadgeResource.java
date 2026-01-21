package com.fitpal.service.rest;

import com.fitpal.api.BadgeService;
import com.fitpal.api.dtos.BadgeResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = BadgeResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/badges")
public class BadgeResource {

    @Reference
    private BadgeService badgeService;

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEarnedBadges(@QueryParam("userId") String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            List<BadgeResponse> badges = badgeService.getEarnedBadges(userId);
            return successResponse(200, badges);
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving badges: " + e.getMessage());
        }
    }
}
