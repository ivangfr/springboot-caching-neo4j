package com.mycompany.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantRequest {

    @Schema(example = "cityId")
    private UUID cityId;

    @Schema(example = "Happy Sushi")
    private String name;
}
