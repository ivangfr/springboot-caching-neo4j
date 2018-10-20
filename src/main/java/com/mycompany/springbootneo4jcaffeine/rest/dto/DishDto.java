package com.mycompany.springbootneo4jcaffeine.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishDto {

    private String id;
    private String name;
    private BigDecimal price;

}
