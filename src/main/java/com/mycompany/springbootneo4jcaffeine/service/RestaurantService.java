package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;

import java.util.UUID;

public interface RestaurantService {

    Restaurant saveRestaurant(Restaurant restaurant);

    void deleteRestaurant(Restaurant restaurant);

    Restaurant validateAndGetRestaurantById(UUID restaurantId) throws RestaurantNotFoundException;
}
