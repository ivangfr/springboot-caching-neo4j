package com.mycompany.springbootneo4jcaffeine.repository;

import com.mycompany.springbootneo4jcaffeine.model.Meal;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MealRepository extends Neo4jRepository<Meal, String> {
}
