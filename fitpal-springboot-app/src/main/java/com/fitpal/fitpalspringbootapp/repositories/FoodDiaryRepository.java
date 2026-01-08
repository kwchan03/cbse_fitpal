package com.fitpal.fitpalspringbootapp.repositories;

import com.fitpal.fitpalspringbootapp.models.FoodDiary;
import com.fitpal.fitpalspringbootapp.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodDiaryRepository extends MongoRepository<FoodDiary, String> {

    Optional<FoodDiary> findByUserAndDate(User user, LocalDate date);

    List<FoodDiary> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
