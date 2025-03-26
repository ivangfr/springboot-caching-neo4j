package com.ivanfranchin.restaurantapi;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public interface MyContainers {

    @Container
    @ServiceConnection
    Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.26.4")
            .withoutAuthentication();

    @Container
    @ServiceConnection
    GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
            .withExposedPorts(6379);
}
