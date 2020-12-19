package com.mycompany.restaurantapi.service;

import com.mycompany.restaurantapi.exception.DishNotFoundException;
import com.mycompany.restaurantapi.exception.RestaurantNotFoundException;
import com.mycompany.restaurantapi.model.Dish;
import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.repository.DishRepository;
import com.mycompany.restaurantapi.repository.RestaurantRepository;
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
