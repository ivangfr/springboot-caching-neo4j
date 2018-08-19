package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateMealDto {

    @ApiModelProperty(value = "the meal name", example = "Pizza Peperoni")
    private String name;

    @ApiModelProperty(value = "the meal price", example = "6.50")
    private BigDecimal price;

}
