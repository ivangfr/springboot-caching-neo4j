package com.ivanfranchin.restaurantapi.rest.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record CityResponse(
        UUID id, String name,
        List<Restaurant> restaurants) implements Serializable {

    public record Restaurant(UUID id, String name) implements Serializable {
    }
}
