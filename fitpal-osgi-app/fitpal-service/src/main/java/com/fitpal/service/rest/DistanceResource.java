package com.fitpal.service.rest;

import com.fitpal.api.DistanceService;
import com.fitpal.api.dtos.DistanceResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component(service = DistanceResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/distance")
public class DistanceResource {

    @Reference
    private DistanceService distanceService;

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    @GET
    @Path("/daily")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDailyDistance(@QueryParam("userId") String userId, @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty() || date == null || date.isEmpty()) {
                return errorResponse(400, "userId and date are required");
            }
            double distance = distanceService.getDailyDistance(userId, date);
            return successResponse(200, new DistanceResponse(distance));
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving daily distance: " + e.getMessage());
        }
    }

    @GET
    @Path("/weekly")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeeklyDistance(@QueryParam("userId") String userId, @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty() || date == null || date.isEmpty()) {
                return errorResponse(400, "userId and date are required");
            }
            double distance = distanceService.getWeeklyDistance(userId, date);
            return successResponse(200, new DistanceResponse(distance));
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving weekly distance: " + e.getMessage());
        }
    }

    @GET
    @Path("/monthly")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyDistance(@QueryParam("userId") String userId, @QueryParam("month") String month) {
        try {
            if (userId == null || userId.isEmpty() || month == null || month.isEmpty()) {
                return errorResponse(400, "userId and month are required");
            }
            double distance = distanceService.getMonthlyDistance(userId, month);
            return successResponse(200, new DistanceResponse(distance));
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving monthly distance: " + e.getMessage());
        }
    }
}
