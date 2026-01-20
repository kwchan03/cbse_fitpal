package com.fitpal.service.rest;

import com.fitpal.api.FoodDiary;
import com.fitpal.api.FoodDiaryService;
import com.fitpal.api.dtos.AddFoodRequest;
import com.fitpal.api.dtos.CalorieSummaryResponse;
import com.fitpal.api.dtos.FoodDiaryDTO;
import com.fitpal.api.dtos.RecommendMealRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = FoodDiaryResource.class, property = {"osgi.jaxrs.resource=true"})
@Path("/api/food-diary")
public class FoodDiaryResource {

    @Reference
    private FoodDiaryService foodDiaryService;

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    /**
     * Recommend meals for a day
     */
    @POST
    @Path("/recommend-food")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recommendMeal(
            @QueryParam("userId") String userId,
            RecommendMealRequest request) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            List<FoodDiary.Meal> meals = foodDiaryService.recommendMeal(
                    userId,
                    request.getTargetCalories(),
                    request.getDate()
            );
            return successResponse(200, meals);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Get food diary by date
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiaryByDate(
            @QueryParam("userId") String userId,
            @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (date == null || date.isEmpty()) {
                return errorResponse(400, "date is required");
            }
            FoodDiary diary = foodDiaryService.getDiaryByDate(userId, date);

            // Convert to DTO to avoid LocalDate serialization issues
            FoodDiaryDTO diaryDTO = FoodDiaryDTO.fromEntity(diary);
            return successResponse(200, diaryDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Add food to diary
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFoodToDiary(
            @QueryParam("userId") String userId,
            AddFoodRequest request) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            FoodDiary.Meal meal = foodDiaryService.addFoodToDiary(
                    userId,
                    request.getDate(),
                    request.getType(),
                    request.getMealId(),
                    request.getFoodName()
            );
            return successResponse(200, meal);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Remove food from diary
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFoodFromDiary(
            @QueryParam("userId") String userId,
            @QueryParam("date") String date,
            @QueryParam("type") String type,
            @QueryParam("mealId") Integer mealId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (date == null || date.isEmpty()) {
                return errorResponse(400, "date is required");
            }
            if (type == null || type.isEmpty()) {
                return errorResponse(400, "type is required");
            }
            if (mealId == null) {
                return errorResponse(400, "mealId is required");
            }
            foodDiaryService.removeFoodFromDiary(userId, date, type, mealId);
            return successResponse(200, Map.of("message", "Meal removed from diary"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not available")) {
                return errorResponse(404, e.getMessage());
            } else if (e.getMessage().contains("not found")) {
                return errorResponse(404, e.getMessage());
            }
            return errorResponse(500, "Something went wrong");
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Get calorie summary for a specific day
     */
    @GET
    @Path("/calorie-summary-day")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalorieSummaryByDay(
            @QueryParam("userId") String userId,
            @QueryParam("date") String date) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (date == null || date.isEmpty()) {
                return errorResponse(400, "date is required");
            }
            Integer totalCalories = foodDiaryService.getCalorieSummaryByDay(userId, date);
            return successResponse(200, new CalorieSummaryResponse(totalCalories));
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Get calorie summary (daily or weekly)
     */
    @GET
    @Path("/calorie-summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCalorieSummary(
            @QueryParam("userId") String userId,
            @QueryParam("mode") String mode,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("month") Integer month,
            @QueryParam("year") Integer year) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (mode == null || mode.isEmpty()) {
                return errorResponse(400, "mode is required");
            }
            List<Map> summary = foodDiaryService.getCalorieSummary(
                    userId, mode, startDate, endDate, month, year
            );
            return successResponse(200, summary);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }
}