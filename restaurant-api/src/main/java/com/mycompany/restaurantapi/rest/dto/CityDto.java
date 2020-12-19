package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CityDto {

    private UUID id;
    private String name;
    private List<Restaurant> restaurants;

    @Data
    public static final class Restaurant {
        private UUID id;
        private String name;
    }
}
