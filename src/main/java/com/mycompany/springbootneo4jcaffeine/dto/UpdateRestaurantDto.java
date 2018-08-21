package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateRestaurantDto {

    @ApiModelProperty(value = "restaurant name", example = "Mcdonald's")
    private String name;

    @ApiModelProperty(value = "city id", example = "cityId")
    private String cityId;

}
