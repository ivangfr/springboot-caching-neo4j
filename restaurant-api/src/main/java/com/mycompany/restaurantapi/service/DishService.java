package com.mycompany.restaurantapi.service;

import com.mycompany.restaurantapi.model.Dish;

public interface DishService {

    Dish saveDish(Dish dish);

    void deleteDish(Dish dish);
}
