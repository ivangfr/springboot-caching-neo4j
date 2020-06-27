package com.mycompany.restaurantapi.mapper;

import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.rest.dto.CityDto;
import com.mycompany.restaurantapi.rest.dto.CreateCityDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDto toCityDto(City city);

    City toCity(CreateCityDto createCityDto);

}
