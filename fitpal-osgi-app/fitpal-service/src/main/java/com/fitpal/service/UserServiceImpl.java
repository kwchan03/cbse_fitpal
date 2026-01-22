package com.fitpal.service;

import com.cloudinary.Cloudinary;
import com.fitpal.api.User;
import com.fitpal.api.UserService;
import com.fitpal.service.auth.PasswordService;
import com.fitpal.service.auth.TokenService;
import com.fitpal.service.db.UserRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(service = UserService.class)
public class UserServiceImpl implements UserService {

    @Reference
    private UserRepository userRepository;

    @Reference
    private PasswordService passwordService;

    @Reference
    private TokenService tokenService;

    @Reference
    private CloudinaryService cloudinary;

    // --- 1. Register User (Step 1) ---
    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        // SECURITY: Hash the password before saving
        String hashedPassword = passwordService.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    // --- 2. Register Profile (Step 2 - Onboarding) ---
    @Override
    public User registerUserProfile(String userId, User profileData, byte[] imageBytes) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        user.setFirstName(profileData.getFirstName());
        user.setLastName(profileData.getLastName());
        user.setGender(profileData.getGender());
        user.setDob(profileData.getDob());

        // Note: Image handling logic would go here (saving to disk/DB)
        if (imageBytes != null) {
            String url = cloudinary.uploadImage(imageBytes);
            if (url != null) user.setProfilePictureUrl(url);
        }

        return userRepository.save(user);
    }

    // --- 3. Register Physical Info (Step 3 - Onboarding) ---
    @Override
    public User registerUserPhysicalInfo(String userId, User physicalData) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        user.setWeight(physicalData.getWeight());
        user.setHeight(physicalData.getHeight());
        user.setActivityLevel(physicalData.getActivityLevel());
        user.setWeightGoal(physicalData.getWeightGoal());

        calculateAndSetTargets(user);

        user.setDailyTargetSteps(0);
        user.setDailyTargetActivity(0);

        return userRepository.save(user);
    }

    // --- 4. Get User Info ---
    @Override
    public User getUserInfo(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));
    }

    // --- 5. Update Profile (Edit Settings) ---
    @Override
    public User updateUserProfile(String userId, User profileData, byte[] imageBytes) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        if (profileData.getFirstName() != null) user.setFirstName(profileData.getFirstName());
        if (profileData.getLastName() != null) user.setLastName(profileData.getLastName());
        if (profileData.getGender() != null) user.setGender(profileData.getGender());
        if (profileData.getDob() != null) user.setDob(profileData.getDob());

        if (profileData.getWeight() != null) user.setWeight(profileData.getWeight());
        if (profileData.getHeight() != null) user.setHeight(profileData.getHeight());
        if (profileData.getActivityLevel() != null) user.setActivityLevel(profileData.getActivityLevel());
        if (profileData.getWeightGoal() != null) user.setWeightGoal(profileData.getWeightGoal());

        if (user.getDob() != null && user.getWeight() != null &&
                user.getHeight() != null && user.getGender() != null) {
            calculateAndSetTargets(user);
        }

        if (imageBytes != null) {
            System.out.println(imageBytes);
            String url = cloudinary.uploadImage(imageBytes);
            System.out.println(url);
            if (url != null) user.setProfilePictureUrl(url);
        }

        return userRepository.save(user);
    }

    // --- 6. Change Password ---
    @Override
    public void changePassword(String userId, String currentPassword, String newPassword) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        // SECURITY: Verify old password
        if (!passwordService.checkPassword(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        // SECURITY: Hash new password
        user.setPassword(passwordService.hashPassword(newPassword));
        userRepository.save(user);
    }

    // --- 7. Deactivate Account ---
    @Override
    public void deactivateAccount(String userId, String password) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        // SECURITY: Verify password before deactivation
        if (!passwordService.checkPassword(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        user.setDeactivated(true);
        userRepository.save(user);
    }

    // --- 8. Reactivate Account ---
    @Override
    public String reactivateAccount(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify password
        if (!passwordService.checkPassword(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        // Check if already active
        if (!user.isDeactivated()) { // Note: Spring Boot uses getDeactivated(), usually isDeactivated() for boolean
            throw new RuntimeException("Account is already active");
        }

        // Reactivate
        user.setDeactivated(false);
        userRepository.save(user);

        // MATCH SPRING BOOT: Generate and return the token here
        return tokenService.generateToken(user.getId());
    }

    // --- 9. Delete Account ---
    @Override
    public void deleteAccount(String userId, String password) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        // SECURITY: Verify password before deletion
        if (!passwordService.checkPassword(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        userRepository.deleteById(userId);
    }

    // --- 10. Get Goals ---
    @Override
    public Map<String, Object> getUserGoals(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        Map<String, Object> goals = new HashMap<>();
        goals.put("calories", user.getDailyTargetCalorie() != null ? user.getDailyTargetCalorie() : 0.0);
        goals.put("steps", user.getDailyTargetSteps() != null ? user.getDailyTargetSteps() : 0.0);
        goals.put("activity", user.getDailyTargetActivity() != null ? user.getDailyTargetActivity() : 0.0);

        return goals;
    }

    // --- Helper: Math Calculation ---
    private void calculateAndSetTargets(User user) {
        int age = calculateAge(user.getDob());
        double bmr = 10 * user.getWeight()
                + 6.25 * user.getHeight()
                - 5 * age
                + ("Male".equalsIgnoreCase(user.getGender()) ? 5 : -161);

        double activityMultiplier = (user.getActivityLevel() != null) ? user.getActivityLevel() : 1.2;
        double weightGoalAdjustment = (user.getWeightGoal() != null) ? user.getWeightGoal() : 0;
        int targetCalorie = (int) Math.ceil(bmr * activityMultiplier + weightGoalAdjustment);
        user.setDailyTargetCalorie(targetCalorie);
    }

    private int calculateAge(Date dob) {
        if (dob == null) return 0;
        LocalDate birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}