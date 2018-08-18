package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.model.City;

import java.util.List;
import java.util.Optional;

public interface CityService {

    Optional<City> getCityById(String cityId);

    City saveCity(City city);

    List<City> getCities();

    void deleteCity(City city);

}
