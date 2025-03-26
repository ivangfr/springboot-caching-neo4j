package com.ivanfranchin.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;

public record UpdateDishRequest(
        @Schema(example = "Pizza Peperoni") String name,
        @Schema(example = "6.50") BigDecimal price) implements Serializable {
}
