package com.mycompany.restaurantapi.rest;

import com.mycompany.restaurantapi.mapper.RestaurantMapper;
import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.rest.dto.CreateRestaurantRequest;
import com.mycompany.restaurantapi.rest.dto.RestaurantResponse;
import com.mycompany.restaurantapi.rest.dto.UpdateRestaurantRequest;
import com.mycompany.restaurantapi.service.CityService;
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
import java.util.UUID;

import static com.mycompany.restaurantapi.config.CachingConfig.CITIES;
import static com.mycompany.restaurantapi.config.CachingConfig.RESTAURANTS;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final CityService cityService;
    private final RestaurantMapper restaurantMapper;

    @Cacheable(cacheNames = RESTAURANTS, key = "#restaurantId")
    @GetMapping("/{restaurantId}")
    public RestaurantResponse getRestaurant(@PathVariable UUID restaurantId) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        return restaurantMapper.toRestaurantResponse(restaurant);
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
    public RestaurantResponse createRestaurant(@Valid @RequestBody CreateRestaurantRequest createRestaurantRequest) {
        Restaurant restaurant = restaurantMapper.toRestaurant(createRestaurantRequest);
        restaurant = restaurantService.saveRestaurant(restaurant);

        City city = restaurant.getCity();
        city.getRestaurants().add(restaurant);
        cityService.saveCity(city);

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Caching(
            put = @CachePut(cacheNames = RESTAURANTS, key = "#restaurantId"),
            evict = @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    )
    @PutMapping("/{restaurantId}")
    public RestaurantResponse updateRestaurant(@PathVariable UUID restaurantId,
                                               @Valid @RequestBody UpdateRestaurantRequest updateRestaurantRequest) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);

        boolean handleCitySwap = updateRestaurantRequest.getCityId() != null && !restaurant.getCity().getId().equals(updateRestaurantRequest.getCityId());
        if (handleCitySwap) {
            City oldCity = restaurant.getCity();
            oldCity.getRestaurants().remove(restaurant);
            cityService.saveCity(oldCity);
        }

        restaurantMapper.updateRestaurantFromRequest(updateRestaurantRequest, restaurant);
        restaurant = restaurantService.saveRestaurant(restaurant);

        if (handleCitySwap) {
            City city = restaurant.getCity();
            city.getRestaurants().add(restaurant);
            cityService.saveCity(city);
        }

        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = RESTAURANTS, key = "{#restaurantId,#dishId}"),
            @CacheEvict(cacheNames = RESTAURANTS, key = "#restaurantId"),
            @CacheEvict(cacheNames = CITIES, key = "#result.city.id")
    })
    @DeleteMapping("/{restaurantId}")
    public RestaurantResponse deleteRestaurant(@PathVariable UUID restaurantId) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        restaurantService.deleteRestaurant(restaurant);
        return restaurantMapper.toRestaurantResponse(restaurant);
    }
}
