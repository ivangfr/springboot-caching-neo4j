package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.model.Meal;

public interface MealService {

    Meal saveMeal(Meal meal);

    void deleteMeal(Meal meal);

//    Meal validateAndGetMealById(UUID mealId) throws MealNotFoundException;

}
