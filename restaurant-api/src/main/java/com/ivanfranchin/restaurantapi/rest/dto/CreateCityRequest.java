package com.ivanfranchin.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record CreateCityRequest(
        @Schema(example = "Berlin") @NotBlank String name) implements Serializable {
}
