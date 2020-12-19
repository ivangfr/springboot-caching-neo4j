package com.mycompany.restaurantapi.repository;

import com.mycompany.restaurantapi.model.Dish;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface DishRepository extends Neo4jRepository<Dish, UUID> {
}
