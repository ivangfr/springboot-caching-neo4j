package com.ivanfranchin.restaurantapi.config;

import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.config.AbstractNeo4jConfig;
import org.springframework.stereotype.Component;

// Issue: https://github.com/spring-projects/spring-data-neo4j/issues/2728
// Suggested workaround: https://github.com/spring-projects/spring-data-neo4j/issues/2729#issuecomment-1563853038
@Component
public class Issue2728Config extends AbstractNeo4jConfig {

    @Autowired
    Driver driver;

    @Override
    public Driver driver() {
        return driver;
    }

    @Override
    public Configuration cypherDslConfiguration() {
        return Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
    }
}
