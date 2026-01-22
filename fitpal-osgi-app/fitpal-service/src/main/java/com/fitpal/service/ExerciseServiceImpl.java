package com.fitpal.service;

import com.fitpal.api.ExerciseService;
import com.fitpal.api.dtos.*;
import com.fitpal.api.ExerciseLog;
import com.fitpal.api.User;
import com.fitpal.service.db.ExerciseRepository;
import com.fitpal.service.db.MongoService;
import com.fitpal.service.db.UserRepository;
import com.fitpal.service.util.ExerciseMetaUtil;
import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component(service = ExerciseService.class, immediate = true)
public class ExerciseServiceImpl implements ExerciseService {

    @Reference
    private ExerciseRepository exerciseLogRepository;

    @Reference
    private UserRepository userRepository;

    @Reference
    private MongoService mongoService;

    // Helper methods
    private User requireUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ExerciseLog getOrCreateLog(User user, LocalDate date) {
        return exerciseLogRepository.findByUserAndDate(user, date)
                .orElseGet(() -> {
                    ExerciseLog log = new ExerciseLog();
                    log.setUser(user);
                    log.setDate(date);
                    log.setWorkout(new ArrayList<>());
                    log.setCardio(new ArrayList<>());
                    return log;
                });
    }

    private double getUserWeightOrDefault(User user) {
        try {
            Double w = user.getWeight();
            if (w != null) return w;
        } catch (Exception ignored) {}
        return 70.0;
    }

    private String resolveStartTime(String startTime, String timeAlias) {
        String s1 = (startTime == null) ? "" : startTime.trim();
        String s2 = (timeAlias == null) ? "" : timeAlias.trim();

        if (!s1.isEmpty()) return s1;
        if (!s2.isEmpty()) return s2;

        return null;
    }

    private int computeCaloriesBurned(String cardioName, int durationMinutes, double weightKg) {
        System.out.println("DEBUG: computeCaloriesBurned called - name='" + cardioName + "', duration=" + durationMinutes + ", weight=" + weightKg);
        
        if (durationMinutes <= 0) {
            System.out.println("DEBUG: Duration is 0 or negative, returning 0 calories");
            return 0;
        }
        
        double met = ExerciseMetaUtil.getMetOrDefault(cardioName);
        System.out.println("DEBUG: MET value for '" + cardioName + "' = " + met);
        
        if (met == 0.0) {
            System.out.println("Warning: MET value not found for activity '" + cardioName + "'. Using default MET of 5.0");
            met = 5.0;
        }
        
        double calories = (met * weightKg * durationMinutes) / 60.0;
        System.out.println("DEBUG: Calculated calories = (" + met + " * " + weightKg + " * " + durationMinutes + ") / 60 = " + calories);
        
        int result = (int) Math.round(calories);
        System.out.println("DEBUG: Final calories (rounded) = " + result);
        return result;
    }

    @Override
    public ExerciseLog createExercise(String userId, LogExerciseRequest req) {
        if (req.getDate() == null || req.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("date is required (YYYY-MM-DD)");
        }

        final LocalDate date;
        try {
            date = LocalDate.parse(req.getDate().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
        }

        User user = requireUser(userId);

        ExerciseLog log = getOrCreateLog(user, date);

        if (req.getWorkout() != null) {
            for (LogExerciseRequest.WorkoutItem w : req.getWorkout()) {
                log.getWorkout().add(new ExerciseLog.WorkoutEntry(
                        new ObjectId(),
                        w.getName(),
                        resolveStartTime(w.getStartTime(), w.getTime()),
                        w.getSets(),
                        w.getReps()
                ));
            }
        }

        if (req.getCardio() != null) {
            double weightKg = getUserWeightOrDefault(user);

            for (LogExerciseRequest.CardioItem c : req.getCardio()) {
                int duration = (c.getDuration() == null) ? 0 : c.getDuration();
                int calories = computeCaloriesBurned(c.getName(), duration, weightKg);

                log.getCardio().add(new ExerciseLog.CardioEntry(
                        new ObjectId(),
                        c.getName(),
                        resolveStartTime(c.getStartTime(), c.getTime()),
                        duration,
                        calories
                ));
            }
        }

        return exerciseLogRepository.save(log);
    }

    @Override
    public List<ExerciseLog> getExercises(String userId) {
        User user = requireUser(userId);
        return exerciseLogRepository.findByUserOrderByDateDesc(user);
    }

    @Override
    public User updateTargets(String userId, UpdateTargetsRequest req) {
        if (req.getTargetSteps() == null || req.getWorkoutMinutes() == null || req.getBurnedCalories() == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        User user = requireUser(userId);
        user.setDailyTargetSteps(req.getTargetSteps());
        user.setDailyTargetActivity(req.getWorkoutMinutes());
        user.setDailyTargetCalorie(req.getBurnedCalories());

        return userRepository.save(user);
    }

    @Override
    public Map<String, Integer> fetchCardioDurationToday(String userId) {
        User user = requireUser(userId);
        LocalDate today = LocalDate.now();

        ExerciseLog log = exerciseLogRepository.findByUserAndDate(user, today).orElse(null);
        if (log == null || log.getCardio() == null || log.getCardio().isEmpty()) {
            return Collections.singletonMap("totalDuration", 0);
        }

        int total = log.getCardio().stream()
                .map(ExerciseLog.CardioEntry::getDuration)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return Collections.singletonMap("totalDuration", total);
    }

    @Override
    public Map<String, Integer> fetchCaloriesBurnedToday(String userId) {
        User user = requireUser(userId);
        LocalDate today = LocalDate.now();

        ExerciseLog log = exerciseLogRepository.findByUserAndDate(user, today).orElse(null);
        if (log == null || log.getCardio() == null || log.getCardio().isEmpty()) {
            return Collections.singletonMap("totalCalories", 0);
        }

        int total = log.getCardio().stream()
                .map(ExerciseLog.CardioEntry::getCaloriesBurned)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return Collections.singletonMap("totalCalories", total);
    }

    @Override
    public ExerciseLog recalcCardioCaloriesForDate(String userId, LocalDate date) {
        User user = requireUser(userId);
        ExerciseLog log = exerciseLogRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new NoSuchElementException("Log not found"));

        double weightKg = getUserWeightOrDefault(user);

        if (log.getCardio() != null) {
            for (ExerciseLog.CardioEntry c : log.getCardio()) {
                int duration = c.getDuration() == null ? 0 : c.getDuration();
                int calories = computeCaloriesBurned(c.getName(), duration, weightKg);
                c.setCaloriesBurned(calories);
            }
        }

        return exerciseLogRepository.save(log);
    }

    @Override
    public WeeklySummaryDto fetchWeeklySummary(String userId) {
        User user = requireUser(userId);
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        // Note: StepsService reference would be needed for totalSteps
        // For now, we'll set it to 0
        int totalSteps = 0;

        List<ExerciseLog> logs = findLogsByDateRange(user, start, end);

        int totalMinutes = 0;
        int totalCalories = 0;

        for (ExerciseLog log : logs) {
            int cardioMinutes = (log.getCardio() == null) ? 0 :
                    log.getCardio().stream()
                            .map(ExerciseLog.CardioEntry::getDuration)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .sum();
            totalMinutes += cardioMinutes;

            int dailyCalories = (log.getCardio() == null) ? 0 :
                    log.getCardio().stream()
                            .map(ExerciseLog.CardioEntry::getCaloriesBurned)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .sum();
            totalCalories += dailyCalories;
        }

        int daysCount = 7;

        return new WeeklySummaryDto(
                Math.round(totalSteps / (float) daysCount),
                Math.round(totalMinutes / (float) daysCount),
                Math.round(totalCalories / (float) daysCount)
        );
    }

    private List<ExerciseLog> findLogsByDateRange(User user, LocalDate start, LocalDate end) {
        // Simplified implementation - would use MongoDB query in production
        List<ExerciseLog> allLogs = exerciseLogRepository.findByUserOrderByDateDesc(user);
        List<ExerciseLog> filtered = new ArrayList<>();
        
        for (ExerciseLog log : allLogs) {
            LocalDate logDate = log.getDate();
            if (!logDate.isBefore(start) && !logDate.isAfter(end)) {
                filtered.add(log);
            }
        }
        
        return filtered;
    }

    @Override
    public ExerciseLog updateCardioExercise(String userId, String cardioId, UpdateCardioRequest req) {
        User user = requireUser(userId);
        
        List<ExerciseLog> logs = exerciseLogRepository.findByUserOrderByDateDesc(user);
        ExerciseLog targetLog = null;
        ExerciseLog.CardioEntry targetEntry = null;
        
        for (ExerciseLog log : logs) {
            if (log.getCardio() != null) {
                for (ExerciseLog.CardioEntry c : log.getCardio()) {
                    if (c.getId() != null && c.getId().toHexString().equals(cardioId)) {
                        targetLog = log;
                        targetEntry = c;
                        break;
                    }
                }
                if (targetLog != null) break;
            }
        }
        
        if (targetLog == null || targetEntry == null) {
            throw new NoSuchElementException("Cardio exercise not found");
        }
        
        String startTime = resolveStartTime(req.getStartTime(), req.getTime());
        if (startTime != null) {
            targetEntry.setStartTime(startTime);
        }
        
        int duration = (req.getDuration() == null) ? 0 : req.getDuration();
        targetEntry.setDuration(duration);
        
        int calories = computeCaloriesBurned(
                targetEntry.getName(),
                duration,
                getUserWeightOrDefault(user)
        );
        targetEntry.setCaloriesBurned(calories);
        
        return exerciseLogRepository.save(targetLog);
    }

    @Override
    public ExerciseLog updateWorkoutExercise(String userId, String workoutId, UpdateWorkoutRequest req) {
        User user = requireUser(userId);
        
        if (!ObjectId.isValid(workoutId)) {
            throw new IllegalArgumentException("Invalid workout ID: " + workoutId);
        }
        
        List<ExerciseLog> logs = exerciseLogRepository.findByUserOrderByDateDesc(user);
        ExerciseLog targetLog = null;
        ExerciseLog.WorkoutEntry targetEntry = null;
        
        for (ExerciseLog log : logs) {
            if (log.getWorkout() != null) {
                for (ExerciseLog.WorkoutEntry w : log.getWorkout()) {
                    if (w.getId() != null && w.getId().toHexString().equals(workoutId)) {
                        targetLog = log;
                        targetEntry = w;
                        break;
                    }
                }
                if (targetLog != null) break;
            }
        }
        
        if (targetLog == null || targetEntry == null) {
            throw new NoSuchElementException("Workout exercise not found");
        }
        
        String startTime = resolveStartTime(req.getStartTime(), req.getTime());
        if (startTime != null) {
            targetEntry.setStartTime(startTime);
        }
        
        if (req.getSets() != null) {
            targetEntry.setSets(req.getSets());
        }
        
        if (req.getReps() != null) {
            targetEntry.setReps(req.getReps());
        }
        
        return exerciseLogRepository.save(targetLog);
    }

    @Override
    public ExerciseLog deleteCardioExercise(String userId, String cardioId) {
        User user = requireUser(userId);
        
        List<ExerciseLog> logs = exerciseLogRepository.findByUserOrderByDateDesc(user);
        ExerciseLog targetLog = null;
        
        for (ExerciseLog log : logs) {
            if (log.getCardio() != null) {
                boolean removed = log.getCardio().removeIf(c -> 
                    c.getId() != null && c.getId().toHexString().equals(cardioId)
                );
                if (removed) {
                    targetLog = log;
                    break;
                }
            }
        }
        
        if (targetLog == null) {
            throw new NoSuchElementException("Cardio exercise not found");
        }
        
        exerciseLogRepository.save(targetLog);
        
        // Return the most recent log
        List<ExerciseLog> updated = exerciseLogRepository.findByUserOrderByDateDesc(user);
        return updated.isEmpty() ? null : updated.get(0);
    }

    @Override
    public ExerciseLog deleteWorkoutExercise(String userId, String workoutId) {
        User user = requireUser(userId);
        
        List<ExerciseLog> logs = exerciseLogRepository.findByUserOrderByDateDesc(user);
        ExerciseLog targetLog = null;
        
        for (ExerciseLog log : logs) {
            if (log.getWorkout() != null) {
                boolean removed = log.getWorkout().removeIf(w -> 
                    w.getId() != null && w.getId().toHexString().equals(workoutId)
                );
                if (removed) {
                    targetLog = log;
                    break;
                }
            }
        }
        
        if (targetLog == null) {
            throw new NoSuchElementException("Workout exercise not found");
        }
        
        exerciseLogRepository.save(targetLog);
        
        // Return the most recent log
        List<ExerciseLog> updated = exerciseLogRepository.findByUserOrderByDateDesc(user);
        return updated.isEmpty() ? null : updated.get(0);
    }

    @Override
    public List<Document> getCalorieOutSummary(String userId, String mode) {
        User user = requireUser(userId);

        List<Document> pipeline = new ArrayList<>();

        pipeline.add(new Document("$match",
                new Document("user.$id", new ObjectId(user.getId()))
        ));
        pipeline.add(new Document("$unwind", "$cardio"));
        pipeline.add(new Document("$addFields",
                new Document("parsedDate", new Document("$toDate", "$date"))
        ));

        Document groupId;
        if ("weekly".equalsIgnoreCase(mode)) {
            groupId = new Document("week", new Document("$isoWeek", "$parsedDate"))
                    .append("year", new Document("$isoWeekYear", "$parsedDate"));
        } else {
            groupId = new Document("date",
                    new Document("$dateToString",
                            new Document("format", "%Y-%m-%d").append("date", "$parsedDate")
                    )
            );
        }

        pipeline.add(new Document("$group",
                new Document("_id", groupId)
                        .append("totalCaloriesOut", new Document("$sum", "$cardio.caloriesBurned"))
        ));

        pipeline.add(new Document("$sort",
                "weekly".equalsIgnoreCase(mode)
                        ? new Document("_id.year", 1).append("_id.week", 1)
                        : new Document("_id.date", 1)
        ));

        AggregateIterable<Document> result = mongoService.getDatabase()
                .getCollection("exerciselogs")
                .aggregate(pipeline);
        
        List<Document> results = new ArrayList<>();
        for (Document doc : result) {
            results.add(doc);
        }
        return results;
    }

    @Override
    public List<Document> getCardioVsWorkoutSummary(String userId, String mode, LocalDate startDate, LocalDate endDate) {
        User user = requireUser(userId);

        if (mode == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Missing query parameters");
        }

        List<Document> pipeline = new ArrayList<>();

        pipeline.add(new Document("$match",
                new Document("user.$id", new ObjectId(user.getId()))
        ));

        pipeline.add(new Document("$addFields",
                new Document("parsedDate", new Document("$toDate", "$date"))
        ));

        pipeline.add(new Document("$match",
                new Document("parsedDate",
                        new Document("$gte", Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                .append("$lte", Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                )
        ));

        pipeline.add(new Document("$addFields",
                new Document("totalMinutes",
                        new Document("$sum", new Document("$ifNull", Arrays.asList("$cardio.duration", Collections.emptyList())))
                ).append("totalReps",
                        new Document("$sum",
                                new Document("$map",
                                        new Document("input", new Document("$ifNull", Arrays.asList("$workout", Collections.emptyList())))
                                                .append("as", "w")
                                                .append("in", new Document("$multiply",
                                                        Arrays.asList(
                                                                new Document("$ifNull", Arrays.asList("$$w.sets", 0)),
                                                                new Document("$ifNull", Arrays.asList("$$w.reps", 0))
                                                        )
                                                ))
                                )
                        )
                )
        ));

        Document groupId;
        if ("weekly".equalsIgnoreCase(mode)) {
            groupId = new Document("week", new Document("$isoWeek", "$parsedDate"))
                    .append("year", new Document("$isoWeekYear", "$parsedDate"));
        } else {
            groupId = new Document("date",
                    new Document("$dateToString",
                            new Document("format", "%Y-%m-%d").append("date", "$parsedDate")
                    )
            );
        }

        pipeline.add(new Document("$group",
                new Document("_id", groupId)
                        .append("totalMinutes", new Document("$sum", "$totalMinutes"))
                        .append("totalReps", new Document("$sum", "$totalReps"))
        ));

        pipeline.add(new Document("$sort",
                "weekly".equalsIgnoreCase(mode)
                        ? new Document("_id.year", 1).append("_id.week", 1)
                        : new Document("_id.date", 1)
        ));

        AggregateIterable<Document> result = mongoService.getDatabase()
                .getCollection("exerciselogs")
                .aggregate(pipeline);
        
        List<Document> results = new ArrayList<>();
        for (Document doc : result) {
            results.add(doc);
        }
        return results;
    }
}