package com.mycompany.restaurantapi.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CachingConfig {

    public static final String CITIES = "CITIES";
    public static final String RESTAURANTS = "RESTAURANTS";
    public static final String DISHES = "DISHES";
}
