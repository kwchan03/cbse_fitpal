package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.dtos.BadgeResponse;
import com.fitpal.fitpalspringbootapp.models.Badge;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.repositories.BadgeRepository;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;
    
    public List<BadgeResponse> getEarnedBadges(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Badge> allBadges = badgeRepository.findAll();
        double totalDistance = user.getTotalDistance() != null ? user.getTotalDistance() : 0.0;
        
        return allBadges.stream()
                .filter(badge -> badge.getThreshold() <= totalDistance)
                .map(badge -> new BadgeResponse(badge.getName(), badge.getDescription()))
                .collect(Collectors.toList());
    }
}