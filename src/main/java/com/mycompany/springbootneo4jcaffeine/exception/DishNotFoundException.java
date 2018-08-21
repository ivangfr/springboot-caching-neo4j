package com.mycompany.springbootneo4jcaffeine.exception;

public class DishNotFoundException extends Exception {

    public DishNotFoundException(String dishId) {
        super(String.format("Dish id '%s' not found", dishId));
    }
}
