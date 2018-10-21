package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.repository.DishRepository;
import com.mycompany.springbootneo4jcaffeine.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, DishRepository dishRepository) {
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    public Page<Restaurant> getRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
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
