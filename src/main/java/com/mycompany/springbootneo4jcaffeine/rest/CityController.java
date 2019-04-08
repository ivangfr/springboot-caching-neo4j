package com.mycompany.springbootneo4jcaffeine.rest;

import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CityDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateCityDto;
import com.mycompany.springbootneo4jcaffeine.service.CityService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.mycompany.springbootneo4jcaffeine.config.CacheConfig.CITIES;

@CacheConfig(cacheNames = CITIES)
@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final MapperFacade mapper;
    private final CityService cityService;

    public CityController(MapperFacade mapper, CityService cityService) {
        this.mapper = mapper;
        this.cityService = cityService;
    }

    @Cacheable(key = "#cityId")
    @GetMapping("/{cityId}")
    public CityDto getCity(@PathVariable String cityId) throws CityNotFoundException {
        City city = cityService.validateAndGetCity(cityId);
        return mapper.map(city, CityDto.class);
    }

    @GetMapping
    public Page<City> getCities(Pageable pageable) {
        return cityService.getCities(pageable);
    }

    @CachePut(key = "#result.id")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CityDto createCity(@Valid @RequestBody CreateCityDto createCityDto) {
        City city = mapper.map(createCityDto, City.class);
        city = cityService.saveCity(city);
        return mapper.map(city, CityDto.class);
    }

    @CacheEvict(key = "#cityId")
    @DeleteMapping("/{cityId}")
    public void deleteCity(@PathVariable String cityId) throws CityNotFoundException {
        City city = cityService.validateAndGetCity(cityId);
        cityService.deleteCity(city);
    }

}
