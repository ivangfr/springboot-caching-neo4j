package com.ivanfranchin.restaurantapi;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public interface MyContainers {

    @Container
    @ServiceConnection
    Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.12.0")
            .withoutAuthentication();

    @Container
    @ServiceConnection
    GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2.0"))
            .withExposedPorts(6379);
}
