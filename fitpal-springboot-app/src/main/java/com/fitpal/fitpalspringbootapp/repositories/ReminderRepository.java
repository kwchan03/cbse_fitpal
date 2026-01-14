package com.fitpal.fitpalspringbootapp.repositories;

import com.fitpal.fitpalspringbootapp.models.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends MongoRepository<Reminder, String> {
    List<Reminder> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, String type);
    List<Reminder> findByUserIdAndTypeOrderByDateAscTimeAsc(String userId, String type);
}