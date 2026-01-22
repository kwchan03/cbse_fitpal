package com.fitpal.api.dtos;

public class UpdateCardioRequest {
    private String date;
    private String startTime;
    private String time;
    private Integer duration;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
}