package com.fitpal.service;

import com.fitpal.api.Badge;
import com.fitpal.service.db.BadgeRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Arrays;
import java.util.List;

@Component(immediate = true)
public class BadgeDataInitializer {

    @Reference
    private BadgeRepository badgeRepository;

    @Activate
    public void activate() {
        if (badgeRepository.findAll().isEmpty()) {
            List<Badge> sampleBadges = Arrays.asList(
                    new Badge(null, "Beginner Walker", "Awarded for walking your first kilometer", 1.0),
                    new Badge(null, "Casual Jogger", "Awarded for jogging 5 kilometers", 5.0),
                    new Badge(null, "Dedicated Runner", "Awarded for running 10 kilometers", 10.0),
                    new Badge(null, "Half Marathon Hero", "Awarded for completing 21.1 kilometers", 21.1),
                    new Badge(null, "Marathon Master", "Awarded for completing a full marathon of 42.195 kilometers", 42.195)
            );

            for (Badge badge : sampleBadges) {
                badgeRepository.save(badge);
            }
            System.out.println("Sample badges initialized successfully.");
        } else {
            System.out.println("Badges already exist in the database.");
        }
    }
}
