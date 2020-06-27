package com.mycompany.restaurantapi.mapper;

import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.rest.dto.CreateRestaurantDto;
import com.mycompany.restaurantapi.rest.dto.RestaurantDto;
import com.mycompany.restaurantapi.rest.dto.UpdateRestaurantDto;
import com.mycompany.restaurantapi.service.CityService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CityService.class}
)
public abstract class RestaurantMapper {

    @Autowired
    private CityService cityService;

    public abstract RestaurantDto toRestaurantDto(Restaurant restaurant);

    @Mapping(target = "city", expression = "java(cityService.validateAndGetCity(createRestaurantDto.getCityId()))")
    public abstract Restaurant toRestaurant(CreateRestaurantDto createRestaurantDto);

    @Mapping(target = "city", expression = "java(updateRestaurantDto.getCityId() == null ? restaurant.getCity() : cityService.validateAndGetCity(updateRestaurantDto.getCityId()))")
    public abstract void updateRestaurantFromDto(UpdateRestaurantDto updateRestaurantDto, @MappingTarget Restaurant restaurant);

}
