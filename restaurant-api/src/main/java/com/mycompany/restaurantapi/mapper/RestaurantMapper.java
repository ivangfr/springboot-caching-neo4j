package com.mycompany.restaurantapi.mapper;

import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.rest.dto.CreateRestaurantDto;
import com.mycompany.restaurantapi.rest.dto.RestaurantDto;
import com.mycompany.restaurantapi.rest.dto.UpdateRestaurantDto;
import com.mycompany.restaurantapi.service.CityService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = CityService.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RestaurantMapper {

    RestaurantDto toRestaurantDto(Restaurant restaurant);

    @Mapping(target = "city", source = "cityId")
    Restaurant toRestaurant(CreateRestaurantDto createRestaurantDto);

    @Mapping(target = "city", source = "cityId")
    void updateRestaurantFromDto(UpdateRestaurantDto updateRestaurantDto, @MappingTarget Restaurant restaurant);

}
