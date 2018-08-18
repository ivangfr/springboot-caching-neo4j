package com.mycompany.springbootneo4jcaffeine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
@EnableCaching
public class SpringbootNeo4jCaffeineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootNeo4jCaffeineApplication.class, args);
    }
}
