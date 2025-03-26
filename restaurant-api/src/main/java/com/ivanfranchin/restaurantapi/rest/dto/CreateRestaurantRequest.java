package com.ivanfranchin.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.UUID;

public record CreateRestaurantRequest(
        @Schema(example = "cityId") UUID cityId,
        @Schema(example = "Happy Pizza") @NotBlank String name) implements Serializable {
}
