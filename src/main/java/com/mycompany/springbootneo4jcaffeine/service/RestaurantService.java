package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantService {

    Page<Restaurant> getRestaurants(Pageable pageable);

    Restaurant saveRestaurant(Restaurant restaurant);

    void deleteRestaurant(Restaurant restaurant);

    Restaurant validateAndGetRestaurantById(String restaurantId) throws RestaurantNotFoundException;
}
