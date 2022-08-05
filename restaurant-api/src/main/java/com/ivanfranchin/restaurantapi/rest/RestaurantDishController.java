package com.ivanfranchin.restaurantapi.rest;

import com.ivanfranchin.restaurantapi.exception.DishNotFoundException;
import com.ivanfranchin.restaurantapi.mapper.DishMapper;
import com.ivanfranchin.restaurantapi.model.Dish;
import com.ivanfranchin.restaurantapi.model.Restaurant;
import com.ivanfranchin.restaurantapi.rest.dto.CreateDishRequest;
import com.ivanfranchin.restaurantapi.rest.dto.DishResponse;
import com.ivanfranchin.restaurantapi.rest.dto.RestaurantMenu;
import com.ivanfranchin.restaurantapi.rest.dto.UpdateDishRequest;
import com.ivanfranchin.restaurantapi.service.DishService;
import com.ivanfranchin.restaurantapi.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import java.util.List;
import java.util.UUID;

import static com.ivanfranchin.restaurantapi.config.CachingConfig.DISHES;
import static com.ivanfranchin.restaurantapi.config.CachingConfig.RESTAURANTS;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/dishes")
public class RestaurantDishController {

    private final RestaurantService restaurantService;
    private final DishService dishService;
    private final DishMapper dishMapper;

    @Cacheable(cacheNames = DISHES, key = "{#restaurantId,#dishId}")
    @GetMapping("/{dishId}")
    public DishResponse getRestaurantDish(@PathVariable UUID restaurantId, @PathVariable UUID dishId) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        Dish dish = restaurantService.validateAndGetDish(restaurant, dishId);
        return dishMapper.toDishResponse(dish);
    }

    @Cacheable(cacheNames = DISHES, key = "#restaurantId")
    @GetMapping
    public RestaurantMenu getRestaurantDishes(@PathVariable UUID restaurantId) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        List<DishResponse> dishResponses = restaurant.getDishes()
                .stream()
                .map(dishMapper::toDishResponse)
                .toList();
        return new RestaurantMenu(dishResponses);
    }

    @Caching(
            put = @CachePut(cacheNames = DISHES, key = "{#restaurantId,#result.id}"),
            evict = {
                    @CacheEvict(cacheNames = DISHES, key = "#restaurantId"),
                    @CacheEvict(cacheNames = RESTAURANTS, key = "#restaurantId")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DishResponse createRestaurantDish(@PathVariable UUID restaurantId,
                                             @Valid @RequestBody CreateDishRequest createDishRequest) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        Dish dish = dishMapper.toDish(createDishRequest);
        dish = dishService.saveDish(dish);

        restaurant.getDishes().add(dish);
        restaurantService.saveRestaurant(restaurant);
        return dishMapper.toDishResponse(dish);
    }

    @Caching(
            put = @CachePut(cacheNames = DISHES, key = "{#restaurantId,#dishId}"),
            evict = {
                    @CacheEvict(cacheNames = DISHES, key = "#restaurantId"),
                    @CacheEvict(cacheNames = RESTAURANTS, key = "#restaurantId")
            }
    )
    @PutMapping("/{dishId}")
    public DishResponse updateRestaurantDish(@PathVariable UUID restaurantId, @PathVariable UUID dishId,
                                             @Valid @RequestBody UpdateDishRequest updateDishRequest) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        Dish dish = restaurantService.validateAndGetDish(restaurant, dishId);

        dishMapper.updateDishFromRequest(updateDishRequest, dish);
        dish = dishService.saveDish(dish);
        return dishMapper.toDishResponse(dish);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = DISHES, key = "{#restaurantId,#dishId}"),
            @CacheEvict(cacheNames = DISHES, key = "#restaurantId"),
            @CacheEvict(cacheNames = RESTAURANTS, key = "#restaurantId")
    })
    @DeleteMapping("/{dishId}")
    public DishResponse deleteRestaurantDish(@PathVariable UUID restaurantId, @PathVariable UUID dishId) {
        Restaurant restaurant = restaurantService.validateAndGetRestaurant(restaurantId);
        Dish dish = restaurant.getDishes()
                .stream()
                .filter(m -> m.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new DishNotFoundException(dishId));
        dishService.deleteDish(dish);
        return dishMapper.toDishResponse(dish);
    }
}
