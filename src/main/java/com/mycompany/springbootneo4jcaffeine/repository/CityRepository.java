package com.mycompany.springbootneo4jcaffeine.repository;

import com.mycompany.springbootneo4jcaffeine.model.City;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CityRepository extends Neo4jRepository<City, String> {
}
