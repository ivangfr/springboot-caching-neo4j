package com.mycompany.springbootneo4jcaffeine.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateCityDto {

    @NotNull
    @NotEmpty
    private String name;

}
