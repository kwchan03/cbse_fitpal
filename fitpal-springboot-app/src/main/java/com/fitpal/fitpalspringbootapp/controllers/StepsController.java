package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.LogStepsRequest;
import com.fitpal.fitpalspringbootapp.dtos.LogStepsResponse;
import com.fitpal.fitpalspringbootapp.models.Steps;
import com.fitpal.fitpalspringbootapp.services.StepsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/steps")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StepsController {

    @Autowired
    private StepsService stepsService;

    @PostMapping
    public ResponseEntity<LogStepsResponse> logSteps(@RequestBody LogStepsRequest request) {
        Steps loggedSteps = stepsService.logSteps(request.getUserId(), request.getDate(), request.getSteps());
        LogStepsResponse response = new LogStepsResponse(
            loggedSteps.getId(),
            loggedSteps.getUserId(),
            loggedSteps.getDate(),
            loggedSteps.getSteps()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily")
    public ResponseEntity<Integer> getDailySteps(@RequestParam String userId, @RequestParam String date) {
        int steps = stepsService.getDailySteps(userId, date);
        return ResponseEntity.ok(steps);
    }

    @GetMapping("/weekly")
    public ResponseEntity<Integer> getWeeklySteps(@RequestParam String userId, @RequestParam String date) {
        int steps = stepsService.getWeeklySteps(userId, date);
        return ResponseEntity.ok(steps);
    }

    @GetMapping("/monthly")
    public ResponseEntity<Integer> getMonthlySteps(@RequestParam String userId, @RequestParam String month) {
        int steps = stepsService.getMonthlySteps(userId, month);
        return ResponseEntity.ok(steps);
    }
}
