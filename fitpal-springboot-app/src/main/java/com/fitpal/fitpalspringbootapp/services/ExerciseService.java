package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.dtos.exercise.*;
import com.fitpal.fitpalspringbootapp.models.ExerciseLog;
import com.fitpal.fitpalspringbootapp.models.User;
import com.fitpal.fitpalspringbootapp.repositories.ExerciseLogRepository;
import com.fitpal.fitpalspringbootapp.repositories.UserRepository;
import com.fitpal.fitpalspringbootapp.utils.ExerciseMetaUtil;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseLogRepository exerciseLogRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final StepsService stepsService;

    // ---------- helpers ----------

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
            Object w = user.getWeight();
            if (w instanceof Number n) return n.doubleValue();
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

    // ---------- core: POST /api/exercises ----------

    public ExerciseLog createExercise(String userId, LogExerciseRequest req) {
        if (req.getDate() == null) throw new IllegalArgumentException("date is required");

        User user = requireUser(userId);
        ExerciseLog log = getOrCreateLog(user, req.getDate());

        // workout (append)
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

        // cardio (append + compute calories)
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

    // ---------- GET /api/exercises ----------

    public List<ExerciseLog> getExercises(String userId) {
        User user = requireUser(userId);
        return exerciseLogRepository.findByUserOrderByDateDesc(user);
    }

    // ---------- PUT /api/exercises/target ----------

    public User updateTargets(String userId, UpdateTargetsRequest req) {
        if (req.getTargetSteps() == null || req.getWorkoutMinutes() == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        User user = requireUser(userId);

        user.setDailyTargetSteps(req.getTargetSteps());
        user.setDailyTargetActivity(req.getWorkoutMinutes());

        return userRepository.save(user);
    }

    // ---------- GET /api/exercises/cardio/duration ----------

    public Map<String, Integer> fetchCardioDurationToday(String userId) {
        User user = requireUser(userId);
        LocalDate today = LocalDate.now();

        ExerciseLog log = exerciseLogRepository.findByUserAndDate(user, today).orElse(null);
        if (log == null || log.getCardio() == null || log.getCardio().isEmpty()) {
            return Map.of("totalDuration", 0);
        }

        int total = log.getCardio().stream()
                .map(ExerciseLog.CardioEntry::getDuration)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return Map.of("totalDuration", total);
    }

    // ---------- GET /api/exercises/calories/burned ----------

    public Map<String, Integer> fetchCaloriesBurnedToday(String userId) {
        User user = requireUser(userId);
        LocalDate today = LocalDate.now();

        ExerciseLog log = exerciseLogRepository.findByUserAndDate(user, today).orElse(null);
        if (log == null || log.getCardio() == null || log.getCardio().isEmpty()) {
            return Map.of("totalCalories", 0);
        }

        int total = log.getCardio().stream()
                .map(ExerciseLog.CardioEntry::getCaloriesBurned)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return Map.of("totalCalories", total);
    }

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


    // ---------- GET /api/exercises/summary/weekly?dates=... ----------

    public WeeklySummaryDto fetchWeeklySummary(String userId) {
        User user = requireUser(userId);

        LocalDate today = LocalDate.now();
        LocalDate start = today.with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        Query q = new Query(
                Criteria.where("user.$id").is(new ObjectId(user.getId()))
                        .and("date").gte(start).lte(end)
        );

        List<ExerciseLog> logs = mongoTemplate.find(q, ExerciseLog.class, "exerciselogs");

        int totalSteps = stepsService.getWeeklySteps(user.getId(), today.toString());

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


    // ---------- Update embedded entries by subdocument _id ----------

    public ExerciseLog updateCardioExercise(String userId, String cardioId, UpdateCardioRequest req) {
        User user = requireUser(userId);

        Query find = new Query(
                Criteria.where("user.$id").is(new ObjectId(user.getId()))
                        .and("cardio._id").is(new ObjectId(cardioId))
        );

        ExerciseLog existing = mongoTemplate.findOne(find, ExerciseLog.class, "exerciselogs");
        if (existing == null) throw new NoSuchElementException("Cardio exercise not found");

        ExerciseLog.CardioEntry cardioEntry = existing.getCardio().stream()
                .filter(c -> c.getId() != null && c.getId().toHexString().equals(cardioId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cardio entry not found"));

        String startTime = resolveStartTime(req.getStartTime(), req.getTime());

        if (startTime == null) {
            startTime = cardioEntry.getStartTime();
        }

        int duration = (req.getDuration() == null) ? 0 : req.getDuration();

        int calories = computeCaloriesBurned(
                cardioEntry.getName(),
                duration,
                getUserWeightOrDefault(user)
        );

        Update u = new Update();
        if (startTime != null) u.set("cardio.$.startTime", startTime);
        u.set("cardio.$.duration", duration);
        u.set("cardio.$.caloriesBurned", calories);

        mongoTemplate.updateFirst(find, u, "exerciselogs");
        return mongoTemplate.findOne(find, ExerciseLog.class, "exerciselogs");
    }

    public ExerciseLog updateWorkoutExercise(String userId, String workoutId, UpdateWorkoutRequest req) {
        User user = requireUser(userId);
        
        if (!ObjectId.isValid(workoutId)) {
            throw new IllegalArgumentException("Invalid workout ID: " + workoutId);
        }

        Query q = new Query(
                Criteria.where("user.$id").is(new ObjectId(user.getId()))
                        .and("workout._id").is(new ObjectId(workoutId))
        );

        String startTime = resolveStartTime(req.getStartTime(), req.getTime());

        if (startTime == null) {
                startTime = mongoTemplate.findOne(q, ExerciseLog.class, "exerciselogs")
                        .getWorkout().stream()
                        .filter(w -> w.getId() != null && w.getId().toHexString().equals(workoutId))
                        .findFirst()
                        .map(ExerciseLog.WorkoutEntry::getStartTime)
                        .orElse(null);
        }

        Update u = new Update();
        if (startTime != null) u.set("workout.$.startTime", startTime);

        if (req.getSets() != null) u.set("workout.$.sets", req.getSets());
        if (req.getReps() != null) u.set("workout.$.reps", req.getReps());

        var result = mongoTemplate.updateFirst(q, u, "exerciselogs");
        if (result.getMatchedCount() == 0) throw new NoSuchElementException("Workout exercise not found");

        return mongoTemplate.findOne(q, ExerciseLog.class, "exerciselogs");
    }

    public ExerciseLog deleteCardioExercise(String userId, String cardioId) {
        User user = requireUser(userId);

        Query q = new Query(
                Criteria.where("user.$id").is(new ObjectId(user.getId()))
                        .and("cardio._id").is(new ObjectId(cardioId))
        );

        Update u = new Update().pull("cardio", new Document("_id", new ObjectId(cardioId)));

        var result = mongoTemplate.updateFirst(q, u, "exerciselogs");
        if (result.getMatchedCount() == 0) throw new NoSuchElementException("Cardio exercise not found");

        return mongoTemplate.findOne(
                new Query(Criteria.where("user.$id").is(new ObjectId(user.getId())))
                        .with(Sort.by(Sort.Direction.DESC, "date")),
                ExerciseLog.class,
                "exerciselogs"
        );
    }

    public ExerciseLog deleteWorkoutExercise(String userId, String workoutId) {
        User user = requireUser(userId);

        Query q = new Query(
                Criteria.where("user.$id").is(new ObjectId(user.getId()))
                        .and("workout._id").is(new ObjectId(workoutId))
        );

        Update u = new Update().pull("workout", new Document("_id", new ObjectId(workoutId)));

        var result = mongoTemplate.updateFirst(q, u, "exerciselogs");
        if (result.getMatchedCount() == 0) throw new NoSuchElementException("Workout exercise not found");

        return mongoTemplate.findOne(
                new Query(Criteria.where("user.$id").is(new ObjectId(user.getId())))
                        .with(Sort.by(Sort.Direction.DESC, "date")),
                ExerciseLog.class,
                "exerciselogs"
        );
    }

    // ---------- Aggregations (RAW pipelines) ----------

    // /api/exercises/calorie-out-summary?mode=daily|weekly
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

        return mongoTemplate.getCollection("exerciselogs")
                .aggregate(pipeline)
                .into(new ArrayList<>());
    }

    // /api/exercises/cardio-vs-workout-summary?mode=daily|weekly&startDate=...&endDate=...
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
                        new Document("$gte", java.sql.Date.valueOf(startDate))
                                .append("$lte", java.sql.Date.valueOf(endDate))
                )
        ));

        pipeline.add(new Document("$addFields",
                new Document("totalMinutes",
                        new Document("$sum", new Document("$ifNull", List.of("$cardio.duration", List.of())))
                ).append("totalReps",
                        new Document("$sum",
                                new Document("$map",
                                        new Document("input", new Document("$ifNull", List.of("$workout", List.of())))
                                                .append("as", "w")
                                                .append("in", new Document("$multiply",
                                                        List.of(
                                                                new Document("$ifNull", List.of("$$w.sets", 0)),
                                                                new Document("$ifNull", List.of("$$w.reps", 0))
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

        return mongoTemplate.getCollection("exerciselogs")
                .aggregate(pipeline)
                .into(new ArrayList<>());
        }

}
