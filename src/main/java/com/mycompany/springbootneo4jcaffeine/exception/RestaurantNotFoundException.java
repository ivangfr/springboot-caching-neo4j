package com.mycompany.springbootneo4jcaffeine.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends Exception {

    public RestaurantNotFoundException(UUID restaurantId) {
        super(String.format("Restaurant id '%s' not found", restaurantId));
    }
}
