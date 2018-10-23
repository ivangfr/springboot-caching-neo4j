package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {

    City saveCity(City city);

    Page<City> getCities(Pageable pageable);

    void deleteCity(City city);

    City validateAndGetCity(String cityId) throws CityNotFoundException;

}
