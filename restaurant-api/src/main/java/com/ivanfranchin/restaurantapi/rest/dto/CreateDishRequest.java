package com.ivanfranchin.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

public record CreateDishRequest(
        @Schema(example = "Pizza Salami") @NotBlank String name,
        @Schema(example = "5.50") @NotNull BigDecimal price) implements Serializable {
}
