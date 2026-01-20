package com.fitpal.service.rest;

import com.fitpal.api.MealService;
import com.fitpal.api.User;
import com.fitpal.api.dtos.AddToFavouriteRequest;
import com.fitpal.api.dtos.MealSearchResponse;
import com.fitpal.api.dtos.MealSearchResult;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component(service = MealResource.class, property = {"osgi.jaxrs.resource=true"})
@Path("/api/meal")
public class MealResource {

    @Reference
    private MealService mealService;

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    /**
     * Search for meals
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMeal(
            @QueryParam("userId") String userId,
            @QueryParam("query") String query,
            @QueryParam("page") Integer page) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (query == null || query.isEmpty()) {
                return errorResponse(400, "query is required");
            }
            MealSearchResponse response = mealService.searchMeal(query, page);
            return successResponse(200, response);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Get meal details by ID
     */
    @GET
    @Path("/{mealId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMealById(
            @QueryParam("userId") String userId,
            @PathParam("mealId") Integer mealId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (mealId == null) {
                return errorResponse(400, "mealId is required");
            }
            MealSearchResult meal = mealService.getMealById(mealId);
            return successResponse(200, meal);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Get nutrition label image
     */
    @GET
    @Path("/nutrition/{mealId}")
    @Produces("image/png")
    public Response getNutritionImage(
            @QueryParam("userId") String userId,
            @PathParam("mealId") Integer mealId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (mealId == null) {
                return errorResponse(400, "mealId is required");
            }
            byte[] image = mealService.getNutritionImage(mealId);

            return Response.ok(image)
                    .header(HttpHeaders.CONTENT_TYPE, "image/png")
                    .header(HttpHeaders.CONTENT_LENGTH, image.length)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Get recipe card
     */
    @GET
    @Path("/recipe/{mealId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipeImage(
            @QueryParam("userId") String userId,
            @PathParam("mealId") Integer mealId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (mealId == null) {
                return errorResponse(400, "mealId is required");
            }
            String recipeCard = mealService.getRecipeImage(mealId);
            return successResponse(200, recipeCard);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Get user's favourite meals
     */
    @GET
    @Path("/favourites")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavouriteMeals(
            @QueryParam("userId") String userId,
            @QueryParam("page") Integer page) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            MealSearchResponse response = mealService.getFavouriteMeals(userId, page);
            return successResponse(200, response);
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Add meal to favourites
     */
    @POST
    @Path("/favourites")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMealToFavourite(
            @QueryParam("userId") String userId,
            AddToFavouriteRequest request) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            User user = mealService.addMealToFavourite(
                    userId,
                    request.getMealId(),
                    request.getFoodName(),
                    request.getImageUrl()
            );
            return successResponse(200, user);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                return errorResponse(404, e.getMessage());
            } else if (e.getMessage().equals("Meal already in favourites")) {
                return errorResponse(400, e.getMessage());
            }
            return errorResponse(500, "Something went wrong");
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }

    /**
     * Remove meal from favourites
     */
    @DELETE
    @Path("/favourites/{mealId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeMealFromFavourite(
            @QueryParam("userId") String userId,
            @PathParam("mealId") Integer mealId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (mealId == null) {
                return errorResponse(400, "mealId is required");
            }
            mealService.removeMealFromFavourite(userId, mealId);
            return successResponse(200, Map.of("message", "Meal removed from favourites"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                return errorResponse(404, e.getMessage());
            }
            return errorResponse(500, "Something went wrong");
        } catch (Exception e) {
            e.printStackTrace();
            return errorResponse(500, "Something went wrong");
        }
    }
}