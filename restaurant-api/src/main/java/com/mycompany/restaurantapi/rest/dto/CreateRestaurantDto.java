package com.mycompany.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class CreateRestaurantDto {

    @Schema(example = "cityId")
    private UUID cityId;

    @Schema(example = "Happy Pizza")
    @NotBlank
    private String name;

}
