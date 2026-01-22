package com.fitpal.fitpalspringbootapp.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "preferences")
public class Preference {
    @Id
    private String id;
    private String userId;
    private Boolean pushEnabled = true;
    private Boolean emailEnabled = false;
    private Boolean doNotDisturb = false;
}