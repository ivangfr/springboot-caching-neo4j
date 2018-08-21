package com.mycompany.springbootneo4jcaffeine.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ResponseRestaurantDto {

    private String id;

    private String name;

    private RestaurantCityDto city;

    private String address;

    private String email;

    private Set<RestaurantDishDto> dishes;

    @Data
    public static final class RestaurantCityDto {

        private String id;

        private String name;

    }

    @Data
    public static final class RestaurantDishDto {

        private String id;

        private String name;

    }

}
