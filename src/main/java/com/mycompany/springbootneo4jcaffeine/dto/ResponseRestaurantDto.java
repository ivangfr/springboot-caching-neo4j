package com.mycompany.springbootneo4jcaffeine.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ResponseRestaurantDto {

    private String id;

    private String name;

    private RestaurantCityDto city;

    private Set<RestaurantMealDto> meals;

    @Data
    public static final class RestaurantCityDto {

        private String id;

        private String name;

    }

    @Data
    public static final class RestaurantMealDto {

        private String id;

        private String name;

    }

}
