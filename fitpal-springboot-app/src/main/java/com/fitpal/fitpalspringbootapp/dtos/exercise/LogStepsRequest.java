package com.fitpal.fitpalspringbootapp.dtos.exercise;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LogStepsRequest {
    private LocalDate date;
    private Integer steps;
}
