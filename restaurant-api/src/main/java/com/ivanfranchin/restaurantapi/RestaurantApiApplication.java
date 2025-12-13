package com.ivanfranchin.restaurantapi;

import com.ivanfranchin.restaurantapi.config.NativeRuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(NativeRuntimeHintsRegistrar.class)
@SpringBootApplication
public class RestaurantApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApiApplication.class, args);
    }
}
