package com.fitpal.api;

public class Preference {
    private String id;
    private String userId;
    private Boolean pushEnabled = true;
    private Boolean emailEnabled = false;
    private Boolean doNotDisturb = false;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Boolean getPushEnabled() { return pushEnabled; }
    public void setPushEnabled(Boolean pushEnabled) { this.pushEnabled = pushEnabled; }
    public Boolean getEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(Boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public Boolean getDoNotDisturb() { return doNotDisturb; }
    public void setDoNotDisturb(Boolean doNotDisturb) { this.doNotDisturb = doNotDisturb; }
}