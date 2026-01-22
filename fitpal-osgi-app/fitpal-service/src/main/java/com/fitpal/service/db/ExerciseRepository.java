package com.fitpal.service.db;

import com.fitpal.api.ExerciseLog;
import com.fitpal.api.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component(service = ExerciseRepository.class)
public class ExerciseRepository {

    @Reference
    private MongoService mongoService;

    private MongoCollection<Document> getCollection() {
        return mongoService.getDatabase().getCollection("exerciselogs");
    }

    public ExerciseLog save(ExerciseLog log) {
        MongoCollection<Document> coll = getCollection();
        Document doc = toDocument(log);

        if (log.getId() == null) {
            coll.insertOne(doc);
            log.setId(doc.getObjectId("_id").toHexString());
        } else {
            coll.replaceOne(Filters.eq("_id", new ObjectId(log.getId())), doc);
        }
        return log;
    }

    public Optional<ExerciseLog> findByUserAndDate(User user, LocalDate date) {
        Document doc = getCollection().find(
            Filters.and(
                Filters.eq("user.$id", new ObjectId(user.getId())),
                Filters.eq("date", Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            )
        ).first();
        
        return Optional.ofNullable(doc).map(d -> fromDocument(d, user));
    }

    public List<ExerciseLog> findByUserOrderByDateDesc(User user) {
        List<ExerciseLog> logs = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find(Filters.eq("user.$id", new ObjectId(user.getId())))
                .sort(Sorts.descending("date"))
                .iterator()) {
            while (cursor.hasNext()) {
                logs.add(fromDocument(cursor.next(), user));
            }
        }
        return logs;
    }

    private Document toDocument(ExerciseLog log) {
        Document doc = new Document();
        
        if (log.getId() != null) {
            doc.put("_id", new ObjectId(log.getId()));
        }
        
        doc.put("user", new Document("$ref", "users").append("$id", new ObjectId(log.getUser().getId())));
        doc.put("date", Date.from(log.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        List<Document> workoutDocs = new ArrayList<>();
        for (ExerciseLog.WorkoutEntry w : log.getWorkout()) {
            Document wd = new Document()
                .append("_id", w.getId())
                .append("name", w.getName())
                .append("startTime", w.getStartTime())
                .append("sets", w.getSets())
                .append("reps", w.getReps());
            workoutDocs.add(wd);
        }
        doc.put("workout", workoutDocs);
        
        List<Document> cardioDocs = new ArrayList<>();
        for (ExerciseLog.CardioEntry c : log.getCardio()) {
            Document cd = new Document()
                .append("_id", c.getId())
                .append("name", c.getName())
                .append("startTime", c.getStartTime())
                .append("duration", c.getDuration())
                .append("caloriesBurned", c.getCaloriesBurned());
            cardioDocs.add(cd);
        }
        doc.put("cardio", cardioDocs);
        
        return doc;
    }

    private ExerciseLog fromDocument(Document doc, User user) {
        ExerciseLog log = new ExerciseLog();
        log.setId(doc.getObjectId("_id").toHexString());
        log.setUser(user);
        
        Date dateObj = doc.getDate("date");
        if (dateObj != null) {
            log.setDate(dateObj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        List<Document> workoutDocs = (List<Document>) doc.get("workout");
        List<ExerciseLog.WorkoutEntry> workout = new ArrayList<>();
        if (workoutDocs != null) {
            for (Document wd : workoutDocs) {
                workout.add(new ExerciseLog.WorkoutEntry(
                    wd.getObjectId("_id"),
                    wd.getString("name"),
                    wd.getString("startTime"),
                    wd.getInteger("sets"),
                    wd.getInteger("reps")
                ));
            }
        }
        log.setWorkout(workout);
        
        List<Document> cardioDocs = (List<Document>) doc.get("cardio");
        List<ExerciseLog.CardioEntry> cardio = new ArrayList<>();
        if (cardioDocs != null) {
            for (Document cd : cardioDocs) {
                cardio.add(new ExerciseLog.CardioEntry(
                    cd.getObjectId("_id"),
                    cd.getString("name"),
                    cd.getString("startTime"),
                    cd.getInteger("duration"),
                    cd.getInteger("caloriesBurned")
                ));
            }
        }
        log.setCardio(cardio);
        
        return log;
    }
}