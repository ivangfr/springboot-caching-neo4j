package com.mycompany.springbootneo4jcaffeine.repository;

import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface RestaurantRepository extends Neo4jRepository<Restaurant, String> {
}
