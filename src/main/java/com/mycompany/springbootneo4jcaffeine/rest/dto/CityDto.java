package com.mycompany.springbootneo4jcaffeine.rest.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CityDto {

    private String id;
    private String name;
    private Set<Restaurant> restaurants;

    @Data
    public static final class Restaurant {
        private String id;
        private String name;
    }
}
