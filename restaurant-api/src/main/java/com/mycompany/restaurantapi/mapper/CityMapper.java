package com.mycompany.restaurantapi.mapper;

import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.rest.dto.CityResponse;
import com.mycompany.restaurantapi.rest.dto.CreateCityRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityResponse toCityResponse(City city);

    City toCity(CreateCityRequest createCityRequest);
}
