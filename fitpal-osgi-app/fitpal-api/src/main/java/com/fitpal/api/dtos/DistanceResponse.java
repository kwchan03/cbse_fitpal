package com.fitpal.api.dtos;

public class DistanceResponse {

    private double distance;

    public DistanceResponse() {}

    public DistanceResponse(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
