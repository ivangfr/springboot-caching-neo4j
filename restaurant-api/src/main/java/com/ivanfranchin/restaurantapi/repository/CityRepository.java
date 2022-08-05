package com.ivanfranchin.restaurantapi.repository;

import com.ivanfranchin.restaurantapi.model.City;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CityRepository extends Neo4jRepository<City, UUID> {
}
