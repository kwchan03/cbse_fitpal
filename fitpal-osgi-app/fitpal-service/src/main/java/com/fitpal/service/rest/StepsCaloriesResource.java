package com.fitpal.service.rest;

import com.fitpal.api.StepsCaloriesService;
import com.fitpal.api.dtos.StepsCaloriesResponse;
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

@Component(service = StepsCaloriesResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/steps-calories")
public class StepsCaloriesResource {

    @Reference
    private StepsCaloriesService stepsCaloriesService;

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
    public Response getDailyCalories(@QueryParam("userId") String userId, @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty() || date == null || date.isEmpty()) {
                return errorResponse(400, "userId and date are required");
            }
            double calories = stepsCaloriesService.getDailyCalories(userId, date);
            return successResponse(200, new StepsCaloriesResponse(calories));
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving daily calories: " + e.getMessage());
        }
    }

    @GET
    @Path("/weekly")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeeklyCalories(@QueryParam("userId") String userId, @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty() || date == null || date.isEmpty()) {
                return errorResponse(400, "userId and date are required");
            }
            double calories = stepsCaloriesService.getWeeklyCalories(userId, date);
            return successResponse(200, new StepsCaloriesResponse(calories));
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving weekly calories: " + e.getMessage());
        }
    }

    @GET
    @Path("/monthly")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyCalories(@QueryParam("userId") String userId, @QueryParam("month") String month) {
        try {
            if (userId == null || userId.isEmpty() || month == null || month.isEmpty()) {
                return errorResponse(400, "userId and month are required");
            }
            double calories = stepsCaloriesService.getMonthlyCalories(userId, month);
            return successResponse(200, new StepsCaloriesResponse(calories));
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving monthly calories: " + e.getMessage());
        }
    }
}
