package com.ivanfranchin.restaurantapi.service;

import com.ivanfranchin.restaurantapi.exception.DishNotFoundException;
import com.ivanfranchin.restaurantapi.exception.RestaurantNotFoundException;
import com.ivanfranchin.restaurantapi.repository.DishRepository;
import com.ivanfranchin.restaurantapi.repository.RestaurantRepository;
import com.ivanfranchin.restaurantapi.model.Dish;
import com.ivanfranchin.restaurantapi.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @Override
    public Page<Restaurant> getRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteRestaurant(Restaurant restaurant) {
        restaurant.getDishes().forEach(dishRepository::delete);
        restaurantRepository.delete(restaurant);
    }

    @Override
    public Restaurant validateAndGetRestaurant(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    @Override
    public Dish validateAndGetDish(Restaurant restaurant, UUID dishId) {
        return restaurant.getDishes()
                .stream()
                .filter(m -> m.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new DishNotFoundException(dishId));
    }
}
