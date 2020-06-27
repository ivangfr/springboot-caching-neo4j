package com.mycompany.springbootneo4jcaffeine.mapper;

import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CityDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateCityDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDto toCityDto(City city);

    City toCity(CreateCityDto createCityDto);

}
