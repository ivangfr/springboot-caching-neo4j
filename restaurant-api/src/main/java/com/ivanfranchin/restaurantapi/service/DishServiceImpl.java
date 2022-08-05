package com.ivanfranchin.restaurantapi.service;

import com.ivanfranchin.restaurantapi.repository.DishRepository;
import com.ivanfranchin.restaurantapi.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    @Override
    public Dish saveDish(Dish dish) {
        return dishRepository.save(dish);
    }

    @Override
    public void deleteDish(Dish dish) {
        dishRepository.delete(dish);
    }
}
