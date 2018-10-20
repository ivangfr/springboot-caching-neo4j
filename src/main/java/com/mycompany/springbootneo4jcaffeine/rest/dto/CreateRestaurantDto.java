package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateRestaurantDto {

    @ApiModelProperty(value = "city id", example = "cityId")
    @NotNull
    private String cityId;

    @ApiModelProperty(position = 1, value = "restaurant name", example = "Happy Pizza")
    @NotNull
    @NotEmpty
    private String name;

    @ApiModelProperty(position = 2, value = "restaurant address", example = "Jefferson Avenue")
    @NotNull
    @NotEmpty
    private String address;

    @ApiModelProperty(position = 3, value = "restaurant email", example = "happy.pizza@test.com")
    @NotNull
    @NotEmpty
    private String email;

}
