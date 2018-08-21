package com.mycompany.springbootneo4jcaffeine.repository;

import com.mycompany.springbootneo4jcaffeine.model.Dish;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DishRepository extends Neo4jRepository<Dish, String> {
}
