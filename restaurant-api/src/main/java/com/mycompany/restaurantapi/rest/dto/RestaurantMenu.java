package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RestaurantMenu {

    private List<DishDto> dishes = new ArrayList<>();

}
