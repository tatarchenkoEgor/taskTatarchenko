package com.tatarchenko.task.repository;

import com.tatarchenko.task.model.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "histories")
public interface HistoryRepository extends MongoRepository<History, String> {
}
