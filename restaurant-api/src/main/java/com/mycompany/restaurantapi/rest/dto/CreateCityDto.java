package com.mycompany.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateCityDto {

    @Schema(example = "Berlin")
    @NotBlank
    private String name;

}
