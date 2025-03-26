package com.ivanfranchin.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

public record UpdateRestaurantRequest(
        @Schema(example = "cityId") UUID cityId,
        @Schema(example = "Happy Sushi") String name) implements Serializable {
}
