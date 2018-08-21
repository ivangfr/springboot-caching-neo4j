package com.mycompany.springbootneo4jcaffeine.service;

import com.google.common.collect.Sets;
import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.repository.DishRepository;
import com.mycompany.springbootneo4jcaffeine.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, DishRepository dishRepository) {
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    public Set<Restaurant> getRestaurants() {
        return Sets.newHashSet(restaurantRepository.findAll());
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    @Override
    public void deleteRestaurant(Restaurant restaurant) {
        restaurant.getDishes().forEach(dishRepository::delete);
        restaurantRepository.delete(restaurant);
    }

    @Override
    public Restaurant validateAndGetRestaurantById(String restaurantId) throws RestaurantNotFoundException {
        return restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

}
