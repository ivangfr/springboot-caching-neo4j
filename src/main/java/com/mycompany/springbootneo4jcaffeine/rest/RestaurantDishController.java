package com.mycompany.springbootneo4jcaffeine.rest;

import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateDishDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.DishDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.UpdateDishDto;
import com.mycompany.springbootneo4jcaffeine.exception.DishNotFoundException;
import com.mycompany.springbootneo4jcaffeine.exception.RestaurantNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.Dish;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.service.DishService;
import com.mycompany.springbootneo4jcaffeine.service.RestaurantService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@RequestMapping("/api/restaurants/{restaurantId}/dishes")
public class RestaurantDishController {

    private final MapperFacade mapper;
    private final RestaurantService restaurantService;
    private final DishService dishService;

    public RestaurantDishController(MapperFacade mapper, RestaurantService restaurantService, DishService dishService) {
        this.mapper = mapper;
        this.restaurantService = restaurantService;
        this.dishService = dishService;
    }

    @ApiOperation(value = "Get restaurant dish")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Cacheable(key = "{#restaurantId,#dishId}")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{dishId}")
    public DishDto getRestaurantDish(@PathVariable String restaurantId, @PathVariable String dishId)
            throws RestaurantNotFoundException, DishNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = restaurant.getDishes().stream()
                .filter(m -> m.getId().equals(dishId)).findFirst().orElseThrow(() -> new DishNotFoundException(dishId));
        return mapper.map(dish, DishDto.class);
    }

    @ApiOperation(value = "Get restaurant dishes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Cacheable(key = "#restaurantId")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Set<DishDto> getRestaurantDishes(@PathVariable String restaurantId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        return restaurant.getDishes().stream().map(m -> mapper.map(m, DishDto.class)).collect(Collectors.toSet());
    }

    @ApiOperation(value = "Create restaurant dish", code = 201)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Caching(
            put = {@CachePut(key = "{#restaurantId,#result.id}")},
            evict = {@CacheEvict(key = "#restaurantId")}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DishDto createRestaurantDish(@PathVariable String restaurantId, @Valid @RequestBody CreateDishDto createDishDto)
            throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = mapper.map(createDishDto, Dish.class);
        dish = dishService.saveDish(dish);

        restaurant.getDishes().add(dish);
        restaurantService.saveRestaurant(restaurant);
        return mapper.map(dish, DishDto.class);
    }

    @ApiOperation(value = "Update restaurant dish")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Caching(
            put = {@CachePut(key = "{#restaurantId,#dishId}")},
            evict = {@CacheEvict(key = "#restaurantId")}
    )
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{dishId}")
    public DishDto updateRestaurantDish(@PathVariable String restaurantId, @PathVariable String dishId,
                                        @Valid @RequestBody UpdateDishDto updateDishDto)
            throws RestaurantNotFoundException, DishNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = restaurant.getDishes().stream()
                .filter(m -> m.getId().equals(dishId)).findFirst().orElseThrow(() -> new DishNotFoundException(dishId));

        mapper.map(updateDishDto, dish);
        dish = dishService.saveDish(dish);
        return mapper.map(dish, DishDto.class);
    }

    @ApiOperation(value = "Delete restaurant dish")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Caching(evict = {
            @CacheEvict(key = "{#restaurantId,#dishId}"),
            @CacheEvict(key = "#restaurantId")
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{dishId}")
    public void deleteRestaurantDish(@PathVariable String restaurantId, @PathVariable String dishId)
            throws RestaurantNotFoundException, DishNotFoundException {
        Restaurant restaurant = restaurantService.validateAndGetRestaurantById(restaurantId);
        Dish dish = restaurant.getDishes().stream()
                .filter(m -> m.getId().equals(dishId)).findFirst().orElseThrow(() -> new DishNotFoundException(dishId));
        dishService.deleteDish(dish);
    }

}
