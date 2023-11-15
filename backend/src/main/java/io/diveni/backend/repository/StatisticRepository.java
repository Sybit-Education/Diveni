package io.diveni.backend.repository;

import io.diveni.backend.model.Statistic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticRepository extends MongoRepository<Statistic, String> {

}
