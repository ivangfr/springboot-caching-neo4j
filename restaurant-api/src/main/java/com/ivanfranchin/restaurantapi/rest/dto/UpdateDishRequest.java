package com.ivanfranchin.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDishRequest {

    @Schema(example = "Pizza Peperoni")
    private String name;

    @Schema(example = "6.50")
    private BigDecimal price;
}
