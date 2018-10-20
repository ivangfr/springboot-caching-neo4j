package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateDishDto {

    @ApiModelProperty(value = "dish name", example = "Pizza Salami")
    @NotNull
    @NotEmpty
    private String name;

    @ApiModelProperty(position = 2, value = "dish price", example = "5.50")
    @NotNull
    private BigDecimal price;

}
