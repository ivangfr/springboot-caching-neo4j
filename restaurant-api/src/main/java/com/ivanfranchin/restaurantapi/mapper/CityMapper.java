package com.ivanfranchin.restaurantapi.mapper;

import com.ivanfranchin.restaurantapi.model.City;
import com.ivanfranchin.restaurantapi.rest.dto.CityResponse;
import com.ivanfranchin.restaurantapi.rest.dto.CreateCityRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityResponse toCityResponse(City city);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurants", ignore = true)
    City toCity(CreateCityRequest createCityRequest);
}
