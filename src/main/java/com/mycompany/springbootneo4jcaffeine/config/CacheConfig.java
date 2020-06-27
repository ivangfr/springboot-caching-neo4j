package com.mycompany.springbootneo4jcaffeine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

    public static final String CITIES = "CITIES";
    public static final String RESTAURANTS = "RESTAURANTS";
    public static final String DISHES = "DISHES";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(CITIES, RESTAURANTS, DISHES));
        cacheManager.setAllowNullValues(false);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .removalListener((k, v, cause) -> log.info("==> CACHE: Removal Listener called. key={} value={} cause={}", k, v, cause))
                .recordStats();
    }

}
