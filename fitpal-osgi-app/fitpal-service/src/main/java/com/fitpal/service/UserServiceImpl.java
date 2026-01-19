package com.fitpal.service;

import com.fitpal.api.User;
import com.fitpal.api.UserService;
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

    // Removed PasswordService and TokenService references

    // --- 1. Register User (Step 1) ---
    @Override
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("User already exists");
        }
        // No hashing: Save password as plain text
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

        userRepository.save(user);
        return user;
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
        user.setDailyTargetActivity(0.0);

        userRepository.save(user);
        return user;
    }

    // --- 4. Get User Info ---
    @Override
    public User getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));
        return user;
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

        userRepository.save(user);
        return user;
    }

    // --- 6. Change Password ---
    @Override
    public void changePassword(String userId, String currentPassword, String newPassword) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        // REMOVED: Password verification check
        // REMOVED: Password hashing
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    // --- 7. Deactivate Account ---
    @Override
    public void deactivateAccount(String userId, String password) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        // REMOVED: Password verification check
        user.setDeactivated(true);
        userRepository.save(user);
    }

    // --- 8. Reactivate Account ---
    @Override
    public void reactivateAccount(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // REMOVED: Password verification check
        if (!user.isDeactivated()) {
            throw new RuntimeException("Account is already active");
        }

        user.setDeactivated(false);
        userRepository.save(user);
        // REMOVED: Token generation
    }

    // --- 9. Delete Account ---
    @Override
    public void deleteAccount(String userId, String password) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User does not exist"));

        // REMOVED: Password verification check
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
        double targetCalorie = Math.ceil(bmr * activityMultiplier + weightGoalAdjustment);
        user.setDailyTargetCalorie(targetCalorie);
    }

    private int calculateAge(Date dob) {
        if (dob == null) return 0;
        LocalDate birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}