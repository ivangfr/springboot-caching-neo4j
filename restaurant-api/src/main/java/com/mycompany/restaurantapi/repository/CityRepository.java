package com.mycompany.restaurantapi.repository;

import com.mycompany.restaurantapi.model.City;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CityRepository extends Neo4jRepository<City, String> {
}
