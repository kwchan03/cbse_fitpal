package com.fitpal.fitpalspringbootapp.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ReminderDto {
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Date is required")
    private String date;

    @NotBlank(message = "Time is required")
    private String time;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Lead time is required")
    private String leadTime;

    @NotBlank(message = "Recurring is required")
    private String recurring;

    @NotBlank(message = "Notes are required")
    private String notes;

    private Boolean readStatus;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}