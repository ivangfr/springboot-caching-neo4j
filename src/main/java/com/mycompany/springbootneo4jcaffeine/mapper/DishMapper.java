package com.mycompany.springbootneo4jcaffeine.mapper;

import com.mycompany.springbootneo4jcaffeine.model.Dish;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateDishDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.DishDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.UpdateDishDto;
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
