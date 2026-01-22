package com.fitpal.service;

import com.fitpal.api.Badge;
import com.fitpal.api.BadgeService;
import com.fitpal.api.User;
import com.fitpal.api.dtos.BadgeResponse;
import com.fitpal.service.db.BadgeRepository;
import com.fitpal.service.db.UserRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.stream.Collectors;

@Component(service = BadgeService.class)
public class BadgeServiceImpl implements BadgeService {

    @Reference
    private BadgeRepository badgeRepository;

    @Reference
    private UserRepository userRepository;

    @Override
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
