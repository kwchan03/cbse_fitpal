package com.fitpal.fitpalspringbootapp.controllers;

import com.fitpal.fitpalspringbootapp.dtos.BadgeResponse;
import com.fitpal.fitpalspringbootapp.services.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @GetMapping
    public ResponseEntity<List<BadgeResponse>> getEarnedBadges(@RequestParam("userId") String userId) {
        List<BadgeResponse> badges = badgeService.getEarnedBadges(userId);
        return ResponseEntity.ok(badges);
    }
}