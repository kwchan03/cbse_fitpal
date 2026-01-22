package com.fitpal.service.rest;

import com.fitpal.api.Steps;
import com.fitpal.api.StepsService;
import com.fitpal.api.dtos.LogStepsRequest;
import com.fitpal.api.dtos.LogStepsResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component(service = StepsResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/exercises/steps")
public class StepsResource {

    @Reference
    private StepsService stepsService;

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logSteps(LogStepsRequest request, @QueryParam("userId") String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            Steps loggedSteps = stepsService.logSteps(request, userId);
            LogStepsResponse response = new LogStepsResponse(
                loggedSteps.getId(),
                loggedSteps.getUserId(),
                loggedSteps.getDate(),
                loggedSteps.getSteps()
            );
            return successResponse(200, response);
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Failed to log steps: " + e.getMessage());
        }
    }

    @GET
    @Path("/today")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodaySteps(@QueryParam("userId") String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            LocalDate date = LocalDate.now();
            int steps = stepsService.getDailySteps(userId, date.toString());
            Map<String, Integer> response = Map.of("steps", steps);
            return successResponse(200, response);
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving today's steps: " + e.getMessage());
        }
    }

    @GET
    @Path("/daily")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDailySteps(@QueryParam("userId") String userId, @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty() || date == null || date.isEmpty()) {
                return errorResponse(400, "userId and date are required");
            }
            int steps = stepsService.getDailySteps(userId, date);
            return successResponse(200, steps);
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving daily steps: " + e.getMessage());
        }
    }

    @GET
    @Path("/weekly")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeeklySteps(@QueryParam("userId") String userId, @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty() || date == null || date.isEmpty()) {
                return errorResponse(400, "userId and date are required");
            }
            int steps = stepsService.getWeeklySteps(userId, date);
            return successResponse(200, steps);
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving weekly steps: " + e.getMessage());
        }
    }

    @GET
    @Path("/monthly")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlySteps(@QueryParam("userId") String userId, @QueryParam("month") String month) {
        try {
            if (userId == null || userId.isEmpty() || month == null || month.isEmpty()) {
                return errorResponse(400, "userId and month are required");
            }
            int steps = stepsService.getMonthlySteps(userId, month);
            return successResponse(200, steps);
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving monthly steps: " + e.getMessage());
        }
    }
}
