package com.mycompany.springbootneo4jcaffeine.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateRestaurantDto {

    @Schema(example = "cityId")
    @NotNull
    private String cityId;

    @Schema(example = "Happy Pizza")
    @NotBlank
    private String name;

}
