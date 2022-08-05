package com.ivanfranchin.restaurantapi.mapper;

import com.ivanfranchin.restaurantapi.model.Dish;
import com.ivanfranchin.restaurantapi.rest.dto.CreateDishRequest;
import com.ivanfranchin.restaurantapi.rest.dto.DishResponse;
import com.ivanfranchin.restaurantapi.rest.dto.UpdateDishRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DishMapper {

    DishResponse toDishResponse(Dish dish);

    @Mapping(target = "id", ignore = true)
    Dish toDish(CreateDishRequest createDishRequest);

    @Mapping(target = "id", ignore = true)
    void updateDishFromRequest(UpdateDishRequest updateDishRequest, @MappingTarget Dish dish);
}
