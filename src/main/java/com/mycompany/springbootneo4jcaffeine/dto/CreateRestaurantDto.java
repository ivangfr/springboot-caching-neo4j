package com.mycompany.springbootneo4jcaffeine.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateRestaurantDto {

    @ApiModelProperty(value = "the restaurant name", example = "Pizza Hut")
    @NotNull
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "the city id")
    @NotNull
    private UUID cityId;

}
