package com.fitpal.fitpalspringbootapp.config;

import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoDBConnectionTest implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== MongoDB Connection Test ===");

        try {
            // Test connection
            String dbName = mongoTemplate.getDb().getName();
            System.out.println("✅ Connected to database: " + dbName);

            // Check collections
            System.out.println("Collections in database:");
            for (String collectionName : mongoTemplate.getDb().listCollectionNames()) {
                System.out.println("  - " + collectionName);
            }

            // Count users
            long userCount = userRepository.count();
            System.out.println("Total users in User collection: " + userCount);

            // Try to find a user by email (replace with an email you know exists)
            System.out.println("Trying to find users...");
            userRepository.findAll().forEach(user -> {
                System.out.println("Found user: " + user.getEmail());
            });

        } catch (Exception e) {
            System.err.println("❌ MongoDB Connection Failed!");
            e.printStackTrace();
        }

        System.out.println("=== End Connection Test ===");
    }
}