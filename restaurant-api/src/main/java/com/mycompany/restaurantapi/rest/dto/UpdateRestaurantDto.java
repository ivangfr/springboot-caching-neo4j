package com.mycompany.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateRestaurantDto {

    @Schema(example = "cityId")
    private String cityId;

    @Schema(example = "Happy Sushi")
    private String name;

}
