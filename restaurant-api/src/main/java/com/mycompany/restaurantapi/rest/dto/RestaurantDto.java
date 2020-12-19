package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RestaurantDto {

    private UUID id;
    private String name;
    private City city;
    private List<Dish> dishes;

    @Data
    public static final class City {
        private UUID id;
        private String name;
    }

    @Data
    public static final class Dish {
        private UUID id;
        private String name;
    }

}
