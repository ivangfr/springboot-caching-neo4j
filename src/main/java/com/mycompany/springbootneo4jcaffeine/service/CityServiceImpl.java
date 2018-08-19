package com.mycompany.springbootneo4jcaffeine.service;

import com.google.common.collect.Sets;
import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City saveCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public Set<City> getCities() {
        return Sets.newHashSet(cityRepository.findAll());
    }

    @Override
    public void deleteCity(City city) {
        cityRepository.delete(city);
    }

    @Override
    public City validateAndGetCityById(UUID cityId) throws CityNotFoundException {
        return cityRepository.findById(cityId.toString()).orElseThrow(() -> new CityNotFoundException(cityId));
    }

}
