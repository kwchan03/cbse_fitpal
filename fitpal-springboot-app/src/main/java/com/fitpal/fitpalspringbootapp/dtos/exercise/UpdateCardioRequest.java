package com.fitpal.fitpalspringbootapp.dtos.exercise;

import lombok.Data;

@Data
public class UpdateCardioRequest {
    private String date;     
    private String startTime;
    private String time;
    private Integer duration;
}
