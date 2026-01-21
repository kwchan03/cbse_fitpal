package com.fitpal.fitpalspringbootapp.repositories;

import com.fitpal.fitpalspringbootapp.models.Steps;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepsRepository extends MongoRepository<Steps, String> {
    List<Steps> findByUserIdAndDate(String userId, String date);

    List<Steps> findByUserIdAndDateBetween(String userId, String startDate, String endDate);

    List<Steps> findByUserId(String userId);
}
