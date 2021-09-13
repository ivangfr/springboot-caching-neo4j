package com.mycompany.restaurantapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDishRequest {

    @Schema(example = "Pizza Salami")
    @NotBlank
    private String name;

    @Schema(example = "5.50")
    @NotNull
    private BigDecimal price;
}
