package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.LogStepsResponse;
import com.fitpal.fitpalspringbootapp.models.Steps;
import com.fitpal.fitpalspringbootapp.services.StepsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises/steps")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StepsController {

    @Autowired
    private StepsService stepsService;

    @PostMapping
    public ResponseEntity<LogStepsResponse> logSteps(@RequestBody Map<String, Object> request, @RequestAttribute String userId) {
        Integer steps = (Integer) request.get("steps");
        
        String dateStr = (String) request.get("date");
        if (dateStr == null || dateStr.isEmpty()) {
            dateStr = LocalDate.now().toString();
        }
        
        Steps loggedSteps = stepsService.logSteps(userId, dateStr, steps);
        LogStepsResponse response = new LogStepsResponse(
            loggedSteps.getId(),
            loggedSteps.getUserId(),
            loggedSteps.getDate(),
            loggedSteps.getSteps()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<Map<String, Integer>> getTodaySteps(@RequestAttribute String userId) {
        LocalDate date = LocalDate.now();
        int steps = stepsService.getDailySteps(userId, date.toString());
        Map<String, Integer> response = Map.of("steps", steps);
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
