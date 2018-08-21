package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateRestaurantDto {

    @ApiModelProperty(value = "restaurant name", example = "Pizza Hut")
    @NotNull
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "city id", example = "cityId")
    @NotNull
    private String cityId;

}
