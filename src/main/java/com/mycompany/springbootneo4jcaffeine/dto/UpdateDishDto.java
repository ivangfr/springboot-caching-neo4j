package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateDishDto {

    @ApiModelProperty(value = "dish name", example = "Pizza Peperoni")
    private String name;

    @ApiModelProperty(position = 1, value = "dish price", example = "6.50")
    private BigDecimal price;

}
