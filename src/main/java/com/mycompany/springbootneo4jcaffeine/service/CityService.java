package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;

import java.util.Set;
import java.util.UUID;

public interface CityService {

    City saveCity(City city);

    Set<City> getCities();

    void deleteCity(City city);

    City validateAndGetCityById(UUID cityId) throws CityNotFoundException;

}
