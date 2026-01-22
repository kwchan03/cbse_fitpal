package com.fitpal.service.db;

import com.fitpal.api.Reminder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component(service = ReminderRepository.class)
public class ReminderRepository {

    @Reference
    private MongoService mongoService;

    private MongoCollection<Document> getCollection() {
        return mongoService.getDatabase().getCollection("reminders");
    }

    // ========== CRUD Operations ==========

    /**
     * Save reminder (insert new or update existing)
     */
    public Reminder save(Reminder reminder) {
        if (reminder.getId() == null) {
            // Insert new
            reminder.setCreatedAt(LocalDateTime.now());
            reminder.setUpdatedAt(LocalDateTime.now());

            Document doc = mapReminderToDocument(reminder);
            ObjectId newId = new ObjectId();
            doc.append("_id", newId);
            reminder.setId(newId.toString());

            getCollection().insertOne(doc);
        } else {
            // Update existing
            reminder.setUpdatedAt(LocalDateTime.now());
            update(reminder);
        }
        return reminder;
    }

    /**
     * Update existing reminder (internal use)
     */
    private void update(Reminder reminder) {
        if (reminder.getId() == null) {
            throw new IllegalArgumentException("Reminder ID cannot be null for update");
        }
        Document doc = mapReminderToDocument(reminder);
        getCollection().replaceOne(
                Filters.eq("_id", new ObjectId(reminder.getId())),
                doc
        );
    }

    /**
     * Delete reminder by ID
     */
    public void deleteById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        try {
            getCollection().deleteOne(Filters.eq("_id", new ObjectId(id)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ID format", e);
        }
    }

    /**
     * Find reminder by ID
     */
    public Optional<Reminder> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            Document doc = getCollection()
                    .find(Filters.eq("_id", new ObjectId(id)))
                    .first();
            return (doc != null) ? Optional.of(mapDocumentToReminder(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Check if reminder exists
     */
    public boolean existsById(String id) {
        if (id == null) return false;
        try {
            return getCollection().countDocuments(Filters.eq("_id", new ObjectId(id))) > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ========== Custom Queries ==========

    /**
     * Find reminders by userId and type, with sorting support
     * Matches logic needed for getReminders and getNotifications
     */
    public List<Reminder> findByUserIdAndType(String userId, String type, String sortField, boolean isAsc) {
        List<Reminder> list = new ArrayList<>();
        if (userId == null) return list;

        getCollection()
                .find(Filters.and(
                        Filters.eq("userId", userId),
                        Filters.eq("type", type)
                ))
                .sort(isAsc ? Sorts.ascending(sortField) : Sorts.descending(sortField))
                .forEach(doc -> list.add(mapDocumentToReminder(doc)));

        return list;
    }

    public List<Reminder> findByUserIdAndTypeAndReadStatus(String userId, String type, Boolean readStatus) {
        List<Reminder> list = new ArrayList<>();
        getCollection()
                .find(Filters.and(
                        Filters.eq("userId", userId),
                        Filters.eq("type", type),
                        Filters.eq("readStatus", readStatus)
                ))
                .sort(Sorts.descending("createdAt"))
                .forEach(doc -> list.add(mapDocumentToReminder(doc)));
        return list;
    }

    public List<Reminder> findByDateAndTimeAndType(String date, String time, String type) {
        List<Reminder> list = new ArrayList<>();
        getCollection()
                .find(Filters.and(
                        Filters.eq("date", date),
                        Filters.eq("time", time),
                        Filters.eq("type", type)
                ))
                .forEach(doc -> list.add(mapDocumentToReminder(doc)));
        return list;
    }

    // ========== Mappers ==========

    private Document mapReminderToDocument(Reminder r) {
        Document doc = new Document();
        if (r.getId() != null) {
            doc.append("_id", new ObjectId(r.getId()));
        }
        doc.append("userId", r.getUserId());
        doc.append("title", r.getTitle());
        doc.append("date", r.getDate());
        doc.append("time", r.getTime());
        doc.append("category", r.getCategory());
        doc.append("leadTime", r.getLeadTime());
        doc.append("recurring", r.getRecurring());
        doc.append("notes", r.getNotes());
        doc.append("readStatus", r.getReadStatus());
        doc.append("type", r.getType());

        // Handling LocalDateTime -> String (ISO format) for simple storage
        // Alternatively use java.util.Date if your Mongo setup prefers that
        if (r.getCreatedAt() != null) {
            doc.append("createdAt", r.getCreatedAt().toString());
        }
        if (r.getUpdatedAt() != null) {
            doc.append("updatedAt", r.getUpdatedAt().toString());
        }

        return doc;
    }

    private Reminder mapDocumentToReminder(Document doc) {
        Reminder r = new Reminder();
        r.setId(doc.getObjectId("_id").toString());
        r.setUserId(doc.getString("userId"));
        r.setTitle(doc.getString("title"));
        r.setDate(doc.getString("date"));
        r.setTime(doc.getString("time"));
        r.setCategory(doc.getString("category"));
        r.setLeadTime(doc.getString("leadTime"));
        r.setRecurring(doc.getString("recurring"));
        r.setNotes(doc.getString("notes"));

        // Handle Boolean safely
        Boolean readStatus = doc.getBoolean("readStatus");
        r.setReadStatus(readStatus != null ? readStatus : false);

        r.setType(doc.getString("type"));

        // Handle LocalDateTime parsing safely
        r.setCreatedAt(parseDateTimeSafe(doc.getString("createdAt")));
        r.setUpdatedAt(parseDateTimeSafe(doc.getString("updatedAt")));

        return r;
    }

    private LocalDateTime parseDateTimeSafe(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            return null;
        }
    }
}