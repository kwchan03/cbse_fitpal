package com.fitpal.fitpalspringbootapp.repositories;

import com.fitpal.fitpalspringbootapp.models.ExerciseLog;
import com.fitpal.fitpalspringbootapp.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExerciseLogRepository extends MongoRepository<ExerciseLog, String> {

    Optional<ExerciseLog> findByUserAndDate(User user, LocalDate date);

    List<ExerciseLog> findByUserOrderByDateDesc(User user);
}
