package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateRestaurantDto {

    @ApiModelProperty(example = "cityId")
    @NotNull
    private String cityId;

    @ApiModelProperty(position = 1, example = "Happy Pizza")
    @NotBlank
    private String name;

}
