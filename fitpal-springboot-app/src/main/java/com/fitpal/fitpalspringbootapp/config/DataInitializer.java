package com.fitpal.fitpalspringbootapp.config;

import com.fitpal.fitpalspringbootapp.models.Badge;
import com.fitpal.fitpalspringbootapp.repositories.BadgeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BadgeRepository badgeRepository;

    public DataInitializer(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (badgeRepository.count() == 0) {
            List<Badge> sampleBadges = Arrays.asList(
                new Badge(null, "Beginner Walker", "Awarded for walking your first kilometer", 1.0),
                new Badge(null, "Casual Jogger", "Awarded for jogging 5 kilometers", 5.0),
                new Badge(null, "Dedicated Runner", "Awarded for running 10 kilometers", 10.0),
                new Badge(null, "Half Marathon Hero", "Awarded for completing 21.1 kilometers", 21.1),
                new Badge(null, "Marathon Master", "Awarded for completing a full marathon of 42.195 kilometers", 42.195)
            );

            badgeRepository.saveAll(sampleBadges);
            System.out.println("Sample badges initialized successfully.");
        } else {
            System.out.println("Badges already exist in the database.");
        }
    }
}