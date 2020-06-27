package com.mycompany.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateDishDto {

    @Schema(example = "Pizza Peperoni")
    private String name;

    @Schema(example = "6.50")
    private BigDecimal price;

}
