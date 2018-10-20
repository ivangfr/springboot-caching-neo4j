package com.mycompany.springbootneo4jcaffeine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DishNotFoundException extends Exception {

    public DishNotFoundException(String dishId) {
        super(String.format("Dish id '%s' not found", dishId));
    }
}
