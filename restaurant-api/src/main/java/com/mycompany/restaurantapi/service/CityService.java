package com.mycompany.restaurantapi.service;

import com.mycompany.restaurantapi.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {

    City saveCity(City city);

    Page<City> getCities(Pageable pageable);

    void deleteCity(City city);

    City validateAndGetCity(String cityId);

}
