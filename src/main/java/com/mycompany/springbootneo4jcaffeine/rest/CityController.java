package com.mycompany.springbootneo4jcaffeine.rest;

import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateCityDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CityDto;
import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.service.CityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import ma.glasnost.orika.MapperFacade;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import java.util.Set;
import java.util.stream.Collectors;

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

    @ApiOperation(value = "Get city")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Cacheable(key = "#cityId")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{cityId}")
    public CityDto getCity(@PathVariable String cityId) throws CityNotFoundException {
        City city = cityService.validateAndGetCityById(cityId);
        return mapper.map(city, CityDto.class);
    }

    @ApiOperation(value = "Get cities")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Set<CityDto> getCities() {
        return cityService.getCities().stream().map(c -> mapper.map(c, CityDto.class)).collect(Collectors.toSet());
    }

    @ApiOperation(value = "Create city", code = 201)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @CachePut(key = "#result.id")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CityDto createCity(@Valid @RequestBody CreateCityDto createCityDto) {
        City city = mapper.map(createCityDto, City.class);
        city = cityService.saveCity(city);
        return mapper.map(city, CityDto.class);
    }

    @ApiOperation(value = "Delete city")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @CacheEvict(key = "#cityId")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{cityId}")
    public void deleteCity(@PathVariable String cityId) throws CityNotFoundException {
        City city = cityService.validateAndGetCityById(cityId);
        cityService.deleteCity(city);
    }

}
