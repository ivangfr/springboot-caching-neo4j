package com.mycompany.springbootneo4jcaffeine.controller;

import com.mycompany.springbootneo4jcaffeine.dto.CreateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.dto.ResponseRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.dto.UpdateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.service.CityService;
import com.mycompany.springbootneo4jcaffeine.service.RestaurantService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final MapperFacade mapper;
    private final CityService cityService;
    private final RestaurantService restaurantService;

    public RestaurantController(MapperFacade mapper, CityService cityService, RestaurantService restaurantService) {
        this.mapper = mapper;
        this.cityService = cityService;
        this.restaurantService = restaurantService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{restaurantId}")
    public ResponseRestaurantDto getRestaurant(@PathVariable UUID restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        return mapper.map(restaurant, ResponseRestaurantDto.class);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseRestaurantDto createRestaurant(@Valid @RequestBody CreateRestaurantDto createRestaurantDto)
            throws CityNotFoundException {
        Restaurant restaurant = mapper.map(createRestaurantDto, Restaurant.class);

        Restaurant restaurantSaved = restaurantService.saveRestaurant(restaurant);
        return mapper.map(restaurantSaved, ResponseRestaurantDto.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{restaurantId}")
    public ResponseRestaurantDto updateRestaurant(@PathVariable UUID restaurantId, @Valid @RequestBody UpdateRestaurantDto updateRestaurantDto)
            throws CityNotFoundException, RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        mapper.map(updateRestaurantDto, restaurant);

        Restaurant restaurantSaved = restaurantService.saveRestaurant(restaurant);
        return mapper.map(restaurantSaved, ResponseRestaurantDto.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{restaurantId}")
    public void deleteRestaurant(@PathVariable UUID restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        restaurantService.deleteRestaurant(restaurant);
    }

    @ExceptionHandler({CityNotFoundException.class, RestaurantNotFoundException.class})
    public void handleNotFoundException(Exception e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

}
