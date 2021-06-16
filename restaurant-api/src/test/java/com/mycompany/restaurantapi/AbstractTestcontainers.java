package com.mycompany.restaurantapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Slf4j
@Testcontainers
public abstract class AbstractTestcontainers {

    private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.3.0")
            .withoutAuthentication();

    public static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.2.4"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        neo4jContainer.start();
        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);

        if (hasRedisAsProfilesActive()) {
            redisContainer.start();
            registry.add("spring.redis.host", redisContainer::getHost);
            registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
        }
    }

    private static boolean hasRedisAsProfilesActive() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        return List.of(context.getEnvironment().getActiveProfiles()).contains("redis");
    }

}
