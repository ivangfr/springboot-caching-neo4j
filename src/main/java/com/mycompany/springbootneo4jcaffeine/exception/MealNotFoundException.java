package com.mycompany.springbootneo4jcaffeine.exception;

public class MealNotFoundException extends Exception {

    public MealNotFoundException(String mealId) {
        super(String.format("Meal id '%s' not found", mealId));
    }
}
