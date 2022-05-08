package com.mycompany.restaurantapi.mapper;

import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.rest.dto.CityResponse;
import com.mycompany.restaurantapi.rest.dto.CreateCityRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityResponse toCityResponse(City city);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurants", ignore = true)
    City toCity(CreateCityRequest createCityRequest);
}
