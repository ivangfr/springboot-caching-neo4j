package com.mycompany.springbootneo4jcaffeine.controller;

import com.mycompany.springbootneo4jcaffeine.dto.CreateMealDto;
import com.mycompany.springbootneo4jcaffeine.dto.ResponseMealDto;
import com.mycompany.springbootneo4jcaffeine.dto.UpdateMealDto;
import com.mycompany.springbootneo4jcaffeine.exception.MealNotFoundException;
import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Meal;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.service.MealService;
import com.mycompany.springbootneo4jcaffeine.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.cache.CacheManager;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mycompany.springbootneo4jcaffeine.config.CacheConfig.MEALS;

@Slf4j
@CacheConfig(cacheNames = MEALS)
@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/meals")
public class MealController {

    private final CacheManager cacheManager;
    private final MapperFacade mapper;
    private final RestaurantService restaurantService;
    private final MealService mealService;

    public MealController(CacheManager cacheManager, MapperFacade mapper, RestaurantService restaurantService, MealService mealService) {
        this.cacheManager = cacheManager;
        this.mapper = mapper;
        this.restaurantService = restaurantService;
        this.mealService = mealService;
    }

    @Cacheable(key = "{#restaurantId.toString(),#mealId.toString()}")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{mealId}")
    public ResponseMealDto getMealInRestaurant(@PathVariable UUID restaurantId, @PathVariable UUID mealId) throws RestaurantNotFoundException, MealNotFoundException {
        log.info("==> CACHE: Meal id '{}' of the restaurant id '{}' is not cached.", mealId, restaurantId);
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Meal meal = restaurant.getMeals().stream().filter(m -> m.getId().equals(mealId.toString())).findFirst().orElseThrow(() -> new MealNotFoundException(mealId));
        return mapper.map(meal, ResponseMealDto.class);
    }

    @Cacheable(key = "#restaurantId.toString()")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Set<ResponseMealDto> getMealsInRestaurant(@PathVariable UUID restaurantId) throws RestaurantNotFoundException {
        log.info("==> CACHE: List of Meals of the restaurant id '{}' is not cached.", restaurantId);
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        return restaurant.getMeals().stream().map(m -> mapper.map(m, ResponseMealDto.class)).collect(Collectors.toSet());
    }

    @Caching(
            put = {@CachePut(key = "{#restaurantId.toString(),#result.id}")},
            evict = {@CacheEvict(key = "#restaurantId")}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseMealDto createMealInRestaurant(@PathVariable UUID restaurantId, @Valid @RequestBody CreateMealDto createMealDto) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Meal meal = mapper.map(createMealDto, Meal.class);
        meal = mealService.saveMeal(meal);

        restaurant.getMeals().add(meal);
        restaurantService.saveRestaurant(restaurant);
        return mapper.map(meal, ResponseMealDto.class);
    }

    @Caching(evict = {
            @CacheEvict(key = "{#restaurantId.toString(),#mealId.toString()}"),
            @CacheEvict(key = "#restaurantId.toString()")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{mealId}")
    public ResponseMealDto updateMealInRestaurant(@PathVariable UUID restaurantId, @PathVariable UUID mealId, @Valid @RequestBody UpdateMealDto updateMealDto) throws RestaurantNotFoundException, MealNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Meal meal = restaurant.getMeals().stream().filter(m -> m.getId().equals(mealId.toString())).findFirst().orElseThrow(() -> new MealNotFoundException(mealId));

        mapper.map(updateMealDto, meal);
        meal = mealService.saveMeal(meal);
        return mapper.map(meal, ResponseMealDto.class);
    }

    @Caching(evict = {
            @CacheEvict(key = "{#restaurantId.toString(),#mealId.toString()}"),
            @CacheEvict(key = "#restaurantId.toString()")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{mealId}")
    public void deleteMealInRestaurant(@PathVariable UUID restaurantId, @PathVariable UUID mealId) throws RestaurantNotFoundException, MealNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Meal meal = restaurant.getMeals().stream().filter(m -> m.getId().equals(mealId.toString())).findFirst().orElseThrow(() -> new MealNotFoundException(mealId));
        mealService.deleteMeal(meal);
    }

}
