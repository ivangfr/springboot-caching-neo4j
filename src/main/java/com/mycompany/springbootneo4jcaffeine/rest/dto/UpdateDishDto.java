package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateDishDto {

    @ApiModelProperty(example = "Pizza Peperoni")
    private String name;

    @ApiModelProperty(position = 2, example = "6.50")
    private BigDecimal price;

}