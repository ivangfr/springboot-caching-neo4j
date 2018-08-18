package com.mycompany.springbootneo4jcaffeine.controller;

import com.mycompany.springbootneo4jcaffeine.dto.CreateCityDto;
import com.mycompany.springbootneo4jcaffeine.dto.UpdateCityDto;
import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.service.CityService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final MapperFacade mapper;
    private final CityService cityService;

    public CityController(MapperFacade mapper, CityService cityService) {
        this.mapper = mapper;
        this.cityService = cityService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{cityId}")
    public City getCity(@PathVariable UUID cityId) throws CityNotFoundException {
        return cityService.getCityById(cityId.toString())
                .orElseThrow(() -> new CityNotFoundException(cityId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<City> getCities() {
        return cityService.getCities();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public City createCity(@Valid @RequestBody CreateCityDto createCityDto) {
        City city = mapper.map(createCityDto, City.class);
        return cityService.saveCity(city);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{cityId}")
    public City updateCity(@PathVariable UUID cityId, @Valid @RequestBody UpdateCityDto updateCityDto) throws CityNotFoundException {
        City city = cityService.getCityById(cityId.toString())
                .orElseThrow(() -> new CityNotFoundException(cityId));
        mapper.map(updateCityDto, city);
        return cityService.saveCity(city);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{cityId}")
    public void deleteCity(@PathVariable UUID cityId) throws CityNotFoundException {
        City city = cityService.getCityById(cityId.toString())
                .orElseThrow(() -> new CityNotFoundException(cityId));
        cityService.deleteCity(city);
    }

    @ExceptionHandler(CityNotFoundException.class)
    public void handleNotFoundException(Exception e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

}
