package com.fitpal.api;

import com.fitpal.api.dtos.BadgeResponse;

import java.util.List;

public interface BadgeService {
    List<BadgeResponse> getEarnedBadges(String userId);
}
