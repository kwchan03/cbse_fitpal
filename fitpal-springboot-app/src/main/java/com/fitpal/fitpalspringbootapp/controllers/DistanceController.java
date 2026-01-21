package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.DistanceResponse;
import com.fitpal.fitpalspringbootapp.services.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/distance")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DistanceController {

    @Autowired
    private DistanceService distanceService;

    @GetMapping("/daily")
    public ResponseEntity<DistanceResponse> getDailyDistance(
        @RequestParam String userId, @RequestParam String date) {
        double distance = distanceService.getDailyDistance(userId, date);
        return ResponseEntity.ok(new DistanceResponse(distance));
    }

    @GetMapping("/weekly")
    public ResponseEntity<DistanceResponse> getWeeklyDistance(
        @RequestParam String userId, @RequestParam String date) {
        double distance = distanceService.getWeeklyDistance(userId, date);
        return ResponseEntity.ok(new DistanceResponse(distance));
    }

    @GetMapping("/monthly")
    public ResponseEntity<DistanceResponse> getMonthlyDistance(
        @RequestParam String userId, @RequestParam String month) {
        double distance = distanceService.getMonthlyDistance(userId, month);
        return ResponseEntity.ok(new DistanceResponse(distance));
    }
}