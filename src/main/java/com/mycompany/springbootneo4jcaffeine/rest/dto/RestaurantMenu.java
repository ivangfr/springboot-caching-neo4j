package com.mycompany.springbootneo4jcaffeine.rest.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RestaurantMenu {

    private List<DishDto> dishes = new ArrayList<>();

}
