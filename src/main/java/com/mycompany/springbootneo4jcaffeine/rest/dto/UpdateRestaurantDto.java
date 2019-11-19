package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateRestaurantDto {

    @ApiModelProperty(example = "cityId")
    private String cityId;

    @ApiModelProperty(position = 1, example = "Happy Sushi")
    private String name;

}
