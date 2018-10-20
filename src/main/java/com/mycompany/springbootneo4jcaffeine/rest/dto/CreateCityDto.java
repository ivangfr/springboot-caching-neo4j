package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateCityDto {

    @ApiModelProperty(value = "city name", example = "Berlin")
    @NotNull
    @NotEmpty
    private String name;

}
