package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import com.fitpal.fitpalspringbootapp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticate user and handle deactivated accounts
     * @return JWT token if authentication successful
     */
    public String login(String email, String password, Boolean reactivate) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        System.out.println("Found user: " + user.getEmail());
        System.out.println("Stored hash: " + user.getPassword());
        System.out.println("Attempting to match password...");
        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        // Handle deactivated account
        if (user.getDeactivated()) {
            if (reactivate != null && reactivate) {
                // Reactivate the account
                user.setDeactivated(false);
                userRepository.save(user);
            } else {
                // Return specific message for deactivated account
                throw new RuntimeException("Account is deactivated. Please reactivate to continue.");
            }
        }

        // Generate and return JWT token
        return jwtUtil.generateToken(user.getId());
    }
}