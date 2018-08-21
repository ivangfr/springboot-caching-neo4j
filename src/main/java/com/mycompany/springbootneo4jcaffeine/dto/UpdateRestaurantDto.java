package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateRestaurantDto {

    @ApiModelProperty(value = "city id", example = "cityId")
    private String cityId;

    @ApiModelProperty(position = 1, value = "restaurant name", example = "Happy Sushi")
    private String name;

    @ApiModelProperty(position = 2, value = "restaurant address", example = "Halsey Street")
    private String address;

    @ApiModelProperty(position = 3, value = "restaurant email", example = "happy.sushi@test.com")
    private String email;

}
