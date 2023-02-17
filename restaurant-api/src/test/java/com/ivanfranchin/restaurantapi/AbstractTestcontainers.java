package com.ivanfranchin.restaurantapi;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Testcontainers
public abstract class AbstractTestcontainers {

    private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.5.0");
    private static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0.8"));

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        neo4jContainer.withoutAuthentication().start();
        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);

        if (hasRedisAsProfilesActive()) {
            redisContainer.withExposedPorts(6379).start();
            registry.add("spring.redis.host", redisContainer::getHost);
            registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
        }
    }

    private static boolean hasRedisAsProfilesActive() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        return List.of(context.getEnvironment().getActiveProfiles()).contains("redis");
    }
}
