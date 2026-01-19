package com.fitpal.fitpalspringbootapp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "badges")
public class Badge {

    @Id
    private String id;

    private String name;

    private String description;

    private double threshold;
}