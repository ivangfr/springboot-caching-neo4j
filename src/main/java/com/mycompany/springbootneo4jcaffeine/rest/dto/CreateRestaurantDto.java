package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateRestaurantDto {

    @ApiModelProperty(example = "cityId")
    @NotNull
    private String cityId;

    @ApiModelProperty(position = 2, example = "Happy Pizza")
    @NotNull
    @NotEmpty
    private String name;

}
