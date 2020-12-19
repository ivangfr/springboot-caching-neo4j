package com.mycompany.restaurantapi.repository;

import com.mycompany.restaurantapi.model.City;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface CityRepository extends Neo4jRepository<City, UUID> {
}
