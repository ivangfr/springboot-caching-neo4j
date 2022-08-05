package com.ivanfranchin.restaurantapi.service;

import com.ivanfranchin.restaurantapi.model.Dish;

public interface DishService {

    Dish saveDish(Dish dish);

    void deleteDish(Dish dish);
}
