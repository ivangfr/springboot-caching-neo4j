package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateCityDto {

    @ApiModelProperty(value = "the city name", example = "Porto")
    private String name;

}
