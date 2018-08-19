package com.mycompany.springbootneo4jcaffeine.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ResponseCityDto {

    private String id;

    private String name;

    private Set<String> restaurants;

}
