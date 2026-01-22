package com.fitpal.fitpalspringbootapp.dtos;

import lombok.Data;

@Data
public class PreferenceDto {
    private Boolean pushEnabled;
    private Boolean emailEnabled;
    private Boolean doNotDisturb;
}