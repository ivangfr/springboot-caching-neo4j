package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class CityDto {

    private String id;
    private String name;
    private List<Restaurant> restaurants;

    @Data
    public static final class Restaurant {
        private String id;
        private String name;
    }
}
