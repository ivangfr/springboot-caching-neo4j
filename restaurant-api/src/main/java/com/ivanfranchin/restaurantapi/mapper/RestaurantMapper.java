package com.ivanfranchin.restaurantapi.mapper;

import com.ivanfranchin.restaurantapi.service.CityService;
import com.ivanfranchin.restaurantapi.model.Restaurant;
import com.ivanfranchin.restaurantapi.rest.dto.CreateRestaurantRequest;
import com.ivanfranchin.restaurantapi.rest.dto.RestaurantResponse;
import com.ivanfranchin.restaurantapi.rest.dto.UpdateRestaurantRequest;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dishes", ignore = true)
    @Mapping(target = "city", source = "cityId")
    Restaurant toRestaurant(CreateRestaurantRequest createRestaurantRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dishes", ignore = true)
    @Mapping(target = "city", source = "cityId")
    void updateRestaurantFromRequest(UpdateRestaurantRequest updateRestaurantRequest, @MappingTarget Restaurant restaurant);
}
