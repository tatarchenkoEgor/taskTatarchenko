package com.tatarchenko.task.repository;

import com.tatarchenko.task.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {
}
