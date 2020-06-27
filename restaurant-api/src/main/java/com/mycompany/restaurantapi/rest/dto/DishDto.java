package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishDto {

    private String id;
    private String name;
    private BigDecimal price;

}
