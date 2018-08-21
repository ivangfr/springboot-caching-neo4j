package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.model.Dish;

public interface DishService {

    Dish saveDish(Dish dish);

    void deleteDish(Dish dish);

}
