package com.ivanfranchin.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantRequest {

    @Schema(example = "cityId")
    private UUID cityId;

    @Schema(example = "Happy Pizza")
    @NotBlank
    private String name;
}
