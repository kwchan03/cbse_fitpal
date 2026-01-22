package com.fitpal.service.rest;

import com.fitpal.api.Preference;
import com.fitpal.api.Reminder;
import com.fitpal.api.ReminderService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = ReminderResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/reminder")
public class ReminderResource {

    @Reference
    private ReminderService reminderService;

    // ========== Helpers (From UserResource style) ==========

    private Response errorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return Response.status(status).entity(body).build();
    }

    private Response successResponse(int status, Object data) {
        return Response.status(status).entity(data).build();
    }

    // ========== Routes ==========

    // 1. Create
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReminder(@QueryParam("userId") String userId, Reminder reminder) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }
            if (reminder.getTitle() == null || reminder.getDate() == null) {
                return errorResponse(400, "Title and Date are required");
            }

            Reminder created = reminderService.createReminder(reminder, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reminder created successfully!");
            response.put("reminder", created);

            return successResponse(201, response);

        } catch (RuntimeException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Failed to create reminder: " + e.getMessage());
        }
    }

    // 2. Get Reminders
    @GET
    @Path("/get-reminders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReminders(@QueryParam("userId") String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }

            List<Reminder> reminders = reminderService.getReminders(userId);
            return successResponse(200, reminders);

        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving reminders: " + e.getMessage());
        }
    }

    // 3. Get Notifications
    @GET
    @Path("/get-notifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@QueryParam("userId") String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return errorResponse(400, "userId is required");
            }

            List<Reminder> notifications = reminderService.getNotifications(userId);
            return successResponse(200, notifications);

        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Error retrieving notifications: " + e.getMessage());
        }
    }

    // 4. Update Read Status
    @PUT
    @Path("/{id}/read")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReadStatus(@PathParam("id") String id) {
        try {
            if (id == null || id.isEmpty()) {
                return errorResponse(400, "Reminder ID is required");
            }

            Reminder updated = reminderService.updateReadStatus(id);
            return successResponse(200, updated);

        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Failed to update status: " + e.getMessage());
        }
    }

    // 5. Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReminder(@PathParam("id") String id) {
        try {
            if (id == null || id.isEmpty()) {
                return errorResponse(400, "Reminder ID is required");
            }

            reminderService.deleteReminder(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Reminder deleted successfully");
            return successResponse(200, response);

        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Deletion failed: " + e.getMessage());
        }
    }

    // 6. Update Reminder
    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReminder(@PathParam("id") String id, Reminder reminder) {
        try {
            if (id == null || id.isEmpty()) {
                return errorResponse(400, "Reminder ID is required");
            }

            Reminder updated = reminderService.updateReminder(id, reminder);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reminder updated successfully!");
            response.put("reminder", updated);

            return successResponse(200, response);

        } catch (RuntimeException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "Update failed: " + e.getMessage());
        }
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterReminders(
            @QueryParam("userId") String userId,
            @QueryParam("read") Boolean read) {
        try {
            if (userId == null) return errorResponse(400, "userId is required");
            if (read == null) return errorResponse(400, "read status is required");

            List<Reminder> list = reminderService.getRemindersByStatus(userId, read);
            return successResponse(200, list);
        } catch (Exception e) {
            return errorResponse(500, e.getMessage());
        }
    }

    @GET
    @Path("/preferences")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferences(@QueryParam("userId") String userId) {
        try {
            if (userId == null) return errorResponse(400, "userId is required");
            return successResponse(200, reminderService.getPreferences(userId));
        } catch (Exception e) {
            return errorResponse(500, e.getMessage());
        }
    }

    @PUT
    @Path("/preferences")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePreferences(@QueryParam("userId") String userId, Preference prefs) {
        try {
            if (userId == null) return errorResponse(400, "userId is required");

            Preference updated = reminderService.updatePreferences(userId, prefs);
            return successResponse(200, updated);
        } catch (Exception e) {
            return errorResponse(500, e.getMessage());
        }
    }
}