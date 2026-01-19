package com.fitpal.fitpalspringbootapp.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Document(collection = "reminders")
public class Reminder {
    @Id
    private String id;
    private String userId;
    private String title;
    private String date;
    private String time;
    private String category;
    private String leadTime;
    private String recurring;
    private String notes;
    private Boolean readStatus = false;
    private String type = "reminder";
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}