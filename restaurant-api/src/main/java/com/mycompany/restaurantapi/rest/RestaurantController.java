package com.mycompany.restaurantapi.rest;

import com.mycompany.restaurantapi.mapper.RestaurantMapper;
import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.rest.dto.CreateRestaurantDto;
import com.mycompany.restaurantapi.rest.dto.RestaurantDto;
import com.mycompany.restaurantapi.rest.dto.UpdateRestaurantDto;
import com.mycompany.restaurantapi.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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

import static com.mycompany.restaurantapi.config.CacheConfig.CITIES;
import static com.mycompany.restaurantapi.config.CacheConfig.RESTAURANTS;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @Cacheable(cacheNames = RESTAURANTS, key = "#restaurantId")
    @GetMapping("/{restaurantId}")
    public RestaurantDto getRestaurant(@PathVariable String restaurantId) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        return restaurantMapper.toRestaurantDto(restaurant);
    }

    @GetMapping
    public Page<Restaurant> getRestaurants(@ParameterObject Pageable pageable) {
        return restaurantService.getRestaurants(pageable);
    }

    @Caching(
            put = @CachePut(cacheNames = RESTAURANTS, key = "#result.id"),
            evict = @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RestaurantDto createRestaurant(@Valid @RequestBody CreateRestaurantDto createRestaurantDto) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantDto);

        restaurant = restaurantService.saveRestaurant(restaurant);
        return restaurantMapper.toRestaurantDto(restaurant);
    }

    @Transactional
    @Caching(
            put = @CachePut(cacheNames = RESTAURANTS, key = "#restaurantId"),
            evict = @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    )
    @PutMapping("/{restaurantId}")
    public RestaurantDto updateRestaurant(@PathVariable String restaurantId, @Valid @RequestBody UpdateRestaurantDto updateRestaurantDto) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        restaurantMapper.updateRestaurantFromDto(updateRestaurantDto, restaurant);

        restaurant = restaurantService.saveRestaurant(restaurant);
        return restaurantMapper.toRestaurantDto(restaurant);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = RESTAURANTS, key = "{#restaurantId,#dishId}"),
            @CacheEvict(cacheNames = RESTAURANTS, key = "#restaurantId"),
            @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    })
    @DeleteMapping("/{restaurantId}")
    public RestaurantDto deleteRestaurant(@PathVariable String restaurantId) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        restaurantService.deleteRestaurant(restaurant);
        return restaurantMapper.toRestaurantDto(restaurant);
    }

}
