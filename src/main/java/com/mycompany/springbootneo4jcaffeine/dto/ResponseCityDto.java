package com.mycompany.springbootneo4jcaffeine.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ResponseCityDto {

    private String id;

    private String name;

    private Set<CityRestaurantDto> restaurants;

    @Data
    public static final class CityRestaurantDto {

        private String id;

        private String name;

    }
}
