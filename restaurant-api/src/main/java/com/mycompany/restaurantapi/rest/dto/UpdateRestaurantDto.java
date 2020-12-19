package com.mycompany.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateRestaurantDto {

    @Schema(example = "cityId")
    private UUID cityId;

    @Schema(example = "Happy Sushi")
    private String name;

}
