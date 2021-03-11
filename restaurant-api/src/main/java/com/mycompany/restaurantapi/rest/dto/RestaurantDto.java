package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class RestaurantDto implements Serializable {

    private UUID id;
    private String name;
    private City city;
    private List<Dish> dishes;

    @Data
    public static final class City implements Serializable {
        private UUID id;
        private String name;
    }

    @Data
    public static final class Dish implements Serializable {
        private UUID id;
        private String name;
    }

}
