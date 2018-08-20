package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;

import java.util.Set;

public interface CityService {

    City saveCity(City city);

    Set<City> getCities();

    void deleteCity(City city);

    City validateAndGetCityById(String cityId) throws CityNotFoundException;

}
