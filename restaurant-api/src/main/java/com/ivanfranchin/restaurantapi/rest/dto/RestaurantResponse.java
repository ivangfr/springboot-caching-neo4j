package com.ivanfranchin.restaurantapi.rest.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record RestaurantResponse(
        UUID id, String name,
        RestaurantResponse.City city,
        List<Dish> dishes) implements Serializable {

    public record City(UUID id, String name) implements Serializable {
    }

    public record Dish(UUID id, String name) implements Serializable {
    }
}
