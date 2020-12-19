package com.mycompany.restaurantapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
public abstract class AbstractTestcontainers {

    private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.2.1")
            .withoutAuthentication();

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        neo4jContainer.start();
        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
    }

}
