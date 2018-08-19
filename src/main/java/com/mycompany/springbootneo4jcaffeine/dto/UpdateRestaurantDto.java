package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateRestaurantDto {

    @ApiModelProperty(value = "the restaurant name", example = "Mcdonald's")
    private String name;

    @ApiModelProperty(value = "the city id")
    private UUID cityId;

}
