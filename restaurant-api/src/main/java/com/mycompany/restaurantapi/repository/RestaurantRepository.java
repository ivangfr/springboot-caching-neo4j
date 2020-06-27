package com.mycompany.restaurantapi.repository;

import com.mycompany.restaurantapi.model.Restaurant;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface RestaurantRepository extends Neo4jRepository<Restaurant, String> {
}
