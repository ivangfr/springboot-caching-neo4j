package com.mycompany.springbootneo4jcaffeine.exception;

import java.util.UUID;

public class MealNotFoundException extends Exception {

    public MealNotFoundException(UUID mealId) {
        super(String.format("Meal id '%s' not found", mealId));
    }
}
