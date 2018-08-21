package com.mycompany.springbootneo4jcaffeine.controller;

import com.mycompany.springbootneo4jcaffeine.dto.CreateDishDto;
import com.mycompany.springbootneo4jcaffeine.dto.ResponseDishDto;
import com.mycompany.springbootneo4jcaffeine.dto.UpdateDishDto;
import com.mycompany.springbootneo4jcaffeine.exception.DishNotFoundException;
import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Dish;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.service.DishService;
import com.mycompany.springbootneo4jcaffeine.service.RestaurantService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

import static com.mycompany.springbootneo4jcaffeine.config.CacheConfig.DISHES;

@CacheConfig(cacheNames = DISHES)
@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/dishes")
public class RestaurantDishController {

    private final MapperFacade mapper;
    private final RestaurantService restaurantService;
    private final DishService dishService;

    public RestaurantDishController(MapperFacade mapper, RestaurantService restaurantService, DishService dishService) {
        this.mapper = mapper;
        this.restaurantService = restaurantService;
        this.dishService = dishService;
    }

    @Cacheable(key = "{#restaurantId,#dishId}")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{dishId}")
    public ResponseDishDto getDishInRestaurant(@PathVariable String restaurantId, @PathVariable String dishId)
            throws RestaurantNotFoundException, DishNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = restaurant.getDishes().stream()
                .filter(m -> m.getId().equals(dishId)).findFirst().orElseThrow(() -> new DishNotFoundException(dishId));
        return mapper.map(dish, ResponseDishDto.class);
    }

    @Cacheable(key = "#restaurantId")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Set<ResponseDishDto> getDishesInRestaurant(@PathVariable String restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        return restaurant.getDishes().stream().map(m -> mapper.map(m, ResponseDishDto.class)).collect(Collectors.toSet());
    }

    @Caching(
            put = {@CachePut(key = "{#restaurantId,#result.id}")},
            evict = {@CacheEvict(key = "#restaurantId")}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseDishDto createDishInRestaurant(@PathVariable String restaurantId, @Valid @RequestBody CreateDishDto createDishDto)
            throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = mapper.map(createDishDto, Dish.class);
        dish = dishService.saveDish(dish);

        restaurant.getDishes().add(dish);
        restaurantService.saveRestaurant(restaurant);
        return mapper.map(dish, ResponseDishDto.class);
    }

    @Caching(
            put = {@CachePut(key = "{#restaurantId,#dishId}")},
            evict = {@CacheEvict(key = "#restaurantId")}
    )
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{dishId}")
    public ResponseDishDto updateDishInRestaurant(@PathVariable String restaurantId, @PathVariable String dishId, @Valid @RequestBody UpdateDishDto updateDishDto)
            throws RestaurantNotFoundException, DishNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = restaurant.getDishes().stream()
                .filter(m -> m.getId().equals(dishId)).findFirst().orElseThrow(() -> new DishNotFoundException(dishId));

        mapper.map(updateDishDto, dish);
        dish = dishService.saveDish(dish);
        return mapper.map(dish, ResponseDishDto.class);
    }

    @Caching(evict = {
            @CacheEvict(key = "{#restaurantId,#dishId}"),
            @CacheEvict(key = "#restaurantId")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{dishId}")
    public void deleteDishInRestaurant(@PathVariable String restaurantId, @PathVariable String dishId)
            throws RestaurantNotFoundException, DishNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = restaurant.getDishes().stream().filter(m -> m.getId().equals(dishId)).findFirst().orElseThrow(() -> new DishNotFoundException(dishId));
        dishService.deleteDish(dish);
    }

}
