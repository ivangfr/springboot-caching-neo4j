package com.mycompany.restaurantapi.mapper;

import com.mycompany.restaurantapi.model.Dish;
import com.mycompany.restaurantapi.rest.dto.CreateDishDto;
import com.mycompany.restaurantapi.rest.dto.DishDto;
import com.mycompany.restaurantapi.rest.dto.UpdateDishDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DishMapper {

    DishDto toDishDto(Dish dish);

    Dish toDish(CreateDishDto createDishDto);

    void updateDishFromDto(UpdateDishDto updateDishDto, @MappingTarget Dish dish);

}
