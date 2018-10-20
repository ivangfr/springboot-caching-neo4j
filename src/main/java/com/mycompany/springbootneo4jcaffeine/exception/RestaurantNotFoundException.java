package com.mycompany.springbootneo4jcaffeine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RestaurantNotFoundException extends Exception {

    public RestaurantNotFoundException(String restaurantId) {
        super(String.format("Restaurant id '%s' not found", restaurantId));
    }
}
