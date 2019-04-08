package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateCityDto {

    @ApiModelProperty(example = "Berlin")
    @NotBlank
    private String name;

}
