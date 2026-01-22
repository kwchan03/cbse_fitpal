package com.fitpal.service.rest;

import com.fitpal.api.ExerciseLog;
import com.fitpal.api.ExerciseService;
import com.fitpal.api.User;
import com.fitpal.api.dtos.ExerciseLogDTO;
import com.fitpal.api.dtos.LogExerciseRequest;
import com.fitpal.api.dtos.UpdateCardioRequest;
import com.fitpal.api.dtos.UpdateTargetsRequest;
import com.fitpal.api.dtos.UpdateWorkoutRequest;
import com.fitpal.api.dtos.WeeklySummaryDto;
import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(service = ExerciseResource.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=.default)")
@Path("/api/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExerciseResource {

    @Reference
    private ExerciseService exerciseService;

    @POST
    public Response createExercise(
            @HeaderParam("userId") String userId,
            LogExerciseRequest request
    ) {
        try {
            ExerciseLog log = exerciseService.createExercise(userId, request);
            return Response.ok(ExerciseLogDTO.fromEntity(log)).build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return Response.status(400).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to log exercise");
            return Response.status(500).entity(error).build();
        }
    }

    @GET
    public Response getExercises(@HeaderParam("userId") String userId) {
        try {
            List<ExerciseLog> logs = exerciseService.getExercises(userId);

            // Convert entities -> DTOs to avoid LocalDate serialization issues
            List<ExerciseLogDTO> dtoList = logs.stream()
                    .map(ExerciseLogDTO::fromEntity)
                    .collect(Collectors.toList());

            return Response.ok(dtoList).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch exercises");
            return Response.status(500).entity(error).build();
        }
    }

    @PUT
    @Path("/target")
    public Response setDailyTarget(
            @HeaderParam("userId") String userId,
            UpdateTargetsRequest request
    ) {
        try {
            // This endpoint returns User directly; if User contains LocalDate/Instant issues
            // consider introducing a UserDTO similarly. For now keep as-is.
            User updated = exerciseService.updateTargets(userId, request);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return Response.status(400).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Server error");
            return Response.status(500).entity(error).build();
        }
    }

    @GET
    @Path("/cardio/duration")
    public Response fetchCardioDuration(@HeaderParam("userId") String userId) {
        try {
            Map<String, Integer> result = exerciseService.fetchCardioDurationToday(userId);
            return Response.ok(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Server error");
            return Response.status(500).entity(error).build();
        }
    }

    @GET
    @Path("/calories/burned")
    public Response fetchCaloriesBurned(@HeaderParam("userId") String userId) {
        try {
            Map<String, Integer> result = exerciseService.fetchCaloriesBurnedToday(userId);
            return Response.ok(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Server error");
            return Response.status(500).entity(error).build();
        }
    }

    @GET
    @Path("/summary/weekly")
    public Response fetchWeeklyAverages(
            @HeaderParam("userId") String userId,
            @QueryParam("dates") String dates
    ) {
        try {
            // Note: 'dates' currently unused in your service call
            WeeklySummaryDto result = exerciseService.fetchWeeklySummary(userId);
            return Response.ok(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Server error");
            return Response.status(500).entity(error).build();
        }
    }

    @GET
    @Path("/calorie-out-summary")
    public Response getCalorieOutSummary(
            @HeaderParam("userId") String userId,
            @QueryParam("mode") @DefaultValue("daily") String mode
    ) {
        try {
            List<Document> res = exerciseService.getCalorieOutSummary(userId, mode);
            return Response.ok(res).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to summarize calories out");
            return Response.status(500).entity(error).build();
        }
    }

    @GET
    @Path("/cardio-vs-workout-summary")
    public Response getCardioVsWorkoutSummary(
            @HeaderParam("userId") String userId,
            @QueryParam("mode") String mode,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate
    ) {
        try {
            List<Document> res = exerciseService.getCardioVsWorkoutSummary(
                    userId,
                    mode,
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate)
            );
            return Response.ok(res).build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return Response.status(400).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch summary");
            return Response.status(500).entity(error).build();
        }
    }

    @PUT
    @Path("/update/cardio/{id}")
    public Response updateCardioExercise(
            @HeaderParam("userId") String userId,
            @PathParam("id") String id,
            UpdateCardioRequest request
    ) {
        try {
            ExerciseLog result = exerciseService.updateCardioExercise(userId, id, request);
            return Response.ok(ExerciseLogDTO.fromEntity(result)).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update exercise");
            return Response.status(500).entity(error).build();
        }
    }

    @PUT
    @Path("/update/workout/{id}")
    public Response updateWorkoutExercise(
            @HeaderParam("userId") String userId,
            @PathParam("id") String id,
            UpdateWorkoutRequest request
    ) {
        try {
            ExerciseLog result = exerciseService.updateWorkoutExercise(userId, id, request);
            return Response.ok(ExerciseLogDTO.fromEntity(result)).build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return Response.status(400).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update workout exercise");
            return Response.status(500).entity(error).build();
        }
    }

    @DELETE
    @Path("/delete/cardio/{id}")
    public Response deleteCardioExercise(
            @HeaderParam("userId") String userId,
            @PathParam("id") String id
    ) {
        try {
            ExerciseLog updated = exerciseService.deleteCardioExercise(userId, id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cardio exercise deleted successfully");
            response.put("deletedExercise", ExerciseLogDTO.fromEntity(updated));
            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to delete exercise");
            return Response.status(500).entity(error).build();
        }
    }

    @DELETE
    @Path("/delete/workout/{id}")
    public Response deleteWorkoutExercise(
            @HeaderParam("userId") String userId,
            @PathParam("id") String id
    ) {
        try {
            ExerciseLog updated = exerciseService.deleteWorkoutExercise(userId, id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Workout exercise deleted successfully");
            response.put("deletedExercise", ExerciseLogDTO.fromEntity(updated));
            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to delete workout exercise");
            return Response.status(500).entity(error).build();
        }
    }
}
