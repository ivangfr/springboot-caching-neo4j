package com.mycompany.springbootneo4jcaffeine.controller;

import com.mycompany.springbootneo4jcaffeine.dto.CreateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.dto.ResponseRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.dto.UpdateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.service.RestaurantService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mycompany.springbootneo4jcaffeine.config.CacheConfig.RESTAURANTS;

@CacheConfig(cacheNames = RESTAURANTS)
@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final MapperFacade mapper;
    private final RestaurantService restaurantService;

    public RestaurantController(MapperFacade mapper, RestaurantService restaurantService) {
        this.mapper = mapper;
        this.restaurantService = restaurantService;
    }

    @ApiOperation(value = "Get restaurant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Cacheable(key = "#restaurantId")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{restaurantId}")
    public ResponseRestaurantDto getRestaurant(@PathVariable String restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        return mapper.map(restaurant, ResponseRestaurantDto.class);
    }

    @ApiOperation(value = "Get restaurants")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Set<ResponseRestaurantDto> getRestaurants() {
        return restaurantService.getRestaurants().stream().map(r -> mapper.map(r, ResponseRestaurantDto.class)).collect(Collectors.toSet());
    }

    @ApiOperation(value = "Create restaurant", code = 201)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @CachePut(key = "#result.id")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseRestaurantDto createRestaurant(@Valid @RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = mapper.map(createRestaurantDto, Restaurant.class);

        restaurant = restaurantService.saveRestaurant(restaurant);
        return mapper.map(restaurant, ResponseRestaurantDto.class);
    }

    @ApiOperation(value = "Update restaurant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @CachePut(key = "#restaurantId")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{restaurantId}")
    public ResponseRestaurantDto updateRestaurant(@PathVariable String restaurantId, @Valid @RequestBody UpdateRestaurantDto updateRestaurantDto)
            throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        mapper.map(updateRestaurantDto, restaurant);

        restaurant = restaurantService.saveRestaurant(restaurant);
        return mapper.map(restaurant, ResponseRestaurantDto.class);
    }

    @ApiOperation(value = "Delete restaurant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @CacheEvict(key = "#restaurantId")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{restaurantId}")
    public void deleteRestaurant(@PathVariable String restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        restaurantService.deleteRestaurant(restaurant);
    }

}
