package com.mycompany.springbootneo4jcaffeine.exception;

public class RestaurantNotFoundException extends Exception {

    public RestaurantNotFoundException(String restaurantId) {
        super(String.format("Restaurant id '%s' not found", restaurantId));
    }
}
