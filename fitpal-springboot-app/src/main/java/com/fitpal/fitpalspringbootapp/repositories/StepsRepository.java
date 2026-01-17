package com.fitpal.fitpalspringbootapp.repositories;

import com.fitpal.fitpalspringbootapp.models.Steps;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepsRepository extends MongoRepository<Steps, String> {
    @Query("{ 'userId' : ?0, 'date' : { $gte : ?1, $lte : ?2 } }")
    List<Steps> findByUserIdAndDateBetween(String userId, String startDate, String endDate);

    List<Steps> findByUserId(String userId);
}
