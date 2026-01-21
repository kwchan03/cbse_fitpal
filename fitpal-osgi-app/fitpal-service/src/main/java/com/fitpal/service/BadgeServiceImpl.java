package com.fitpal.service;

import com.fitpal.api.Badge;
import com.fitpal.api.BadgeService;
import com.fitpal.api.DistanceService;
import com.fitpal.api.dtos.BadgeResponse;
import com.fitpal.service.db.BadgeRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.stream.Collectors;

@Component(service = BadgeService.class)
public class BadgeServiceImpl implements BadgeService {

    @Reference
    private BadgeRepository badgeRepository;

    @Reference
    private DistanceService distanceService;

    @Override
    public List<BadgeResponse> getEarnedBadges(String userId) {
        List<Badge> allBadges = badgeRepository.findAll();
        double totalDistance = distanceService.getTotalDistance(userId);
        return allBadges.stream()
                .filter(badge -> badge.getThreshold() <= totalDistance)
                .map(badge -> new BadgeResponse(badge.getName(), badge.getDescription()))
                .collect(Collectors.toList());
    }
}
