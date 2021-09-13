package com.mycompany.restaurantapi.mapper;

import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.rest.dto.CreateRestaurantRequest;
import com.mycompany.restaurantapi.rest.dto.RestaurantResponse;
import com.mycompany.restaurantapi.rest.dto.UpdateRestaurantRequest;
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

    RestaurantResponse toRestaurantResponse(Restaurant restaurant);

    @Mapping(target = "city", source = "cityId")
    Restaurant toRestaurant(CreateRestaurantRequest createRestaurantRequest);

    @Mapping(target = "city", source = "cityId")
    void updateRestaurantFromRequest(UpdateRestaurantRequest updateRestaurantRequest,
                                     @MappingTarget Restaurant restaurant);
}
