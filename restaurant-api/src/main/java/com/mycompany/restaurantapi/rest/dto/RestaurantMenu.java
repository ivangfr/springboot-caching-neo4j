package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RestaurantMenu implements Serializable {

    private List<DishResponse> dishes = new ArrayList<>();
}
