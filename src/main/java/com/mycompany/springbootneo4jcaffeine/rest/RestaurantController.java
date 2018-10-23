package com.mycompany.springbootneo4jcaffeine.rest;

import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.RestaurantDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.UpdateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.service.RestaurantService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.mycompany.springbootneo4jcaffeine.config.CacheConfig.CITIES;
import static com.mycompany.springbootneo4jcaffeine.config.CacheConfig.RESTAURANTS;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final MapperFacade mapper;
    private final RestaurantService restaurantService;

    public RestaurantController(MapperFacade mapper, RestaurantService restaurantService) {
        this.mapper = mapper;
        this.restaurantService = restaurantService;
    }

    @Cacheable(cacheNames = RESTAURANTS, key = "#restaurantId")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{restaurantId}")
    public RestaurantDto getRestaurant(@PathVariable String restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        return mapper.map(restaurant, RestaurantDto.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<Restaurant> getRestaurants(Pageable pageable) {
        return restaurantService.getRestaurants(pageable);
    }

    @Caching(
            put = @CachePut(cacheNames = RESTAURANTS, key = "#result.id"),
            evict = @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RestaurantDto createRestaurant(@Valid @RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = mapper.map(createRestaurantDto, Restaurant.class);

        restaurant = restaurantService.saveRestaurant(restaurant);
        return mapper.map(restaurant, RestaurantDto.class);
    }

    @Caching(
            put = @CachePut(cacheNames = RESTAURANTS, key = "#restaurantId"),
            evict = @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    )
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{restaurantId}")
    public RestaurantDto updateRestaurant(@PathVariable String restaurantId, @Valid @RequestBody UpdateRestaurantDto updateRestaurantDto)
            throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        mapper.map(updateRestaurantDto, restaurant);

        restaurant = restaurantService.saveRestaurant(restaurant);
        return mapper.map(restaurant, RestaurantDto.class);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = RESTAURANTS, key = "{#restaurantId,#dishId}"),
            @CacheEvict(cacheNames = RESTAURANTS, key = "#restaurantId"),
            @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{restaurantId}")
    public RestaurantDto deleteRestaurant(@PathVariable String restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        restaurantService.deleteRestaurant(restaurant);
        return mapper.map(restaurant, RestaurantDto.class);
    }

}
