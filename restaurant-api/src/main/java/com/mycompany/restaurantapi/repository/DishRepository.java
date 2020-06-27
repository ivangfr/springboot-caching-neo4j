package com.mycompany.restaurantapi.repository;

import com.mycompany.restaurantapi.model.Dish;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DishRepository extends Neo4jRepository<Dish, String> {
}
