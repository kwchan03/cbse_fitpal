package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.ReminderDto;
import com.fitpal.fitpalspringbootapp.services.ReminderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reminder")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ReminderController {
    @Autowired
    private ReminderService reminderService;

    // 1. Create
    @PostMapping("/create")
    public ResponseEntity<?> createReminder(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody ReminderDto reminderDto
    ) {
        try {
            ReminderDto created = reminderService.createReminder(reminderDto, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reminder created successfully!");
            response.put("reminder", created);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to create reminder"));
        }
    }

    // 2. Get Reminders
    @GetMapping("/get-reminders")
    public ResponseEntity<?> getReminders(
            @RequestAttribute("userId") String userId
    ) {
        return ResponseEntity.ok(reminderService.getReminders(userId));
    }

    // 3. Get Notifications
    @GetMapping("/get-notifications")
    public ResponseEntity<?> getNotifications(
            @RequestAttribute("userId") String userId
    ) {
        return ResponseEntity.ok(reminderService.getNotifications(userId));
    }

    // 4. Update Read Status
    @PutMapping("/{id}/read")
    public ResponseEntity<?> updateReadStatus(@PathVariable String id) {
        try {
            ReminderDto updated = reminderService.updateReadStatus(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable String id) {
        try {
            reminderService.deleteReminder(id);
            return ResponseEntity.ok(Map.of("message", "Reminder deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // 6. Update Reminder
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReminder(@PathVariable String id, @Valid @RequestBody ReminderDto reminderDto) {
        try {
            ReminderDto updated = reminderService.updateReminder(id, reminderDto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reminder updated successfully!");
            response.put("reminder", updated);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}