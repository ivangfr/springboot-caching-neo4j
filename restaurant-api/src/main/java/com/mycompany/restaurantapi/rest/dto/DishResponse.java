package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DishResponse implements Serializable {

    private UUID id;
    private String name;
    private BigDecimal price;
}
