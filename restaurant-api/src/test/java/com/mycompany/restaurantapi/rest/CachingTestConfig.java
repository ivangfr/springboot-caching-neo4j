package com.mycompany.restaurantapi.rest;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mycompany.restaurantapi.config.CachingConfig.CITIES;
import static com.mycompany.restaurantapi.config.CachingConfig.DISHES;
import static com.mycompany.restaurantapi.config.CachingConfig.RESTAURANTS;

@EnableCaching
@Configuration
public class CachingTestConfig {

    @Profile("default")
    @Bean
    public CacheManager concurrentMapCacheManager() {
        return new ConcurrentMapCacheManager(CITIES, RESTAURANTS, DISHES);
    }

    @Profile("caffeine")
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(List.of(CITIES, RESTAURANTS, DISHES));
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(200)
                .expireAfterWrite(5, TimeUnit.MINUTES));
        return cacheManager;
    }
}
