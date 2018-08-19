package com.mycompany.springbootneo4jcaffeine.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResponseMealDto {

    private String id;

    private String name;

    private BigDecimal price;

}
