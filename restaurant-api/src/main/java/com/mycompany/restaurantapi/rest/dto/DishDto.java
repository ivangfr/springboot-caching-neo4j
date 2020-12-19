package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DishDto {

    private UUID id;
    private String name;
    private BigDecimal price;

}
