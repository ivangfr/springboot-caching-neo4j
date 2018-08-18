package com.mycompany.springbootneo4jcaffeine.service;

import com.google.common.collect.Lists;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public Optional<City> getCityById(String cityId) {
        return cityRepository.findById(cityId);
    }

    @Override
    public City saveCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public List<City> getCities() {
        return Lists.newArrayList(cityRepository.findAll());
    }

    @Override
    public void deleteCity(City city) {
        cityRepository.delete(city);
    }

}
