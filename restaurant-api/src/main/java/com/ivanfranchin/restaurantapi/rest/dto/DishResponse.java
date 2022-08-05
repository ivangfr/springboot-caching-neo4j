package com.ivanfranchin.restaurantapi.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record DishResponse(UUID id, String name, BigDecimal price) implements Serializable {
}
