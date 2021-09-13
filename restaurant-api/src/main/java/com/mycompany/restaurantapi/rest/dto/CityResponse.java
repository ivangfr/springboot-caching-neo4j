package com.mycompany.restaurantapi.rest.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class CityResponse implements Serializable {

    private UUID id;
    private String name;
    private List<Restaurant> restaurants;

    @Data
    public static final class Restaurant implements Serializable {
        private UUID id;
        private String name;
    }
}
