package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.dtos.BadgeResponse;
import com.fitpal.fitpalspringbootapp.models.Badge;
import com.fitpal.fitpalspringbootapp.repositories.BadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final DistanceService distanceService;

    public BadgeService(BadgeRepository badgeRepository, DistanceService distanceService) {
        this.badgeRepository = badgeRepository;
        this.distanceService = distanceService;
    }

    public List<BadgeResponse> getEarnedBadges(String userId) {
        List<Badge> allBadges = badgeRepository.findAll();
        double totalDistance = distanceService.getTotalDistance(userId);
        return allBadges.stream()
                .filter(badge -> badge.getThreshold() <= totalDistance)
                .map(badge -> new BadgeResponse(badge.getName(), badge.getDescription()))
                .collect(Collectors.toList());
    }
}