package com.ivanfranchin.restaurantapi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanfranchin.restaurantapi.mapper.DishMapperImpl;
import com.ivanfranchin.restaurantapi.mapper.RestaurantMapperImpl;
import com.ivanfranchin.restaurantapi.model.City;
import com.ivanfranchin.restaurantapi.model.Dish;
import com.ivanfranchin.restaurantapi.model.Restaurant;
import com.ivanfranchin.restaurantapi.rest.dto.CreateDishRequest;
import com.ivanfranchin.restaurantapi.rest.dto.UpdateDishRequest;
import com.ivanfranchin.restaurantapi.service.CityService;
import com.ivanfranchin.restaurantapi.service.DishService;
import com.ivanfranchin.restaurantapi.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static com.ivanfranchin.restaurantapi.config.CachingConfig.DISHES;
import static com.ivanfranchin.restaurantapi.config.CachingConfig.RESTAURANTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisabledIf("#{environment.acceptsProfiles('redis')}")
@AutoConfigureDataNeo4j /* The @AutoConfigureDataNeo4j annotation is used instead of @DataNeo4jTest because both
                           @DataNeo4jTest and @WebMvcTest set @BootstrapWith annotation and having two @BootstrapWith
                           annotations in a test class is not supported. */
@WebMvcTest(controllers = {RestaurantDishController.class, RestaurantController.class})
@Import({RestaurantMapperImpl.class, DishMapperImpl.class, CachingTestConfig.class})
class RestaurantDishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CityService cityService;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private DishService dishService;

    @BeforeEach
    void setUp() {
        cacheManager.getCache(DISHES).clear();
        cacheManager.getCache(RESTAURANTS).clear();
    }

    @Test
    void testGetRestaurantDish() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);
        Dish dish = getDefaultDish(restaurant);

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);
        when(restaurantService.validateAndGetDish(any(Restaurant.class), any(UUID.class))).thenReturn(dish);

        //-- {restaurantId,dishId} cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        //-- {restaurantId,dishId} already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        verify(restaurantService, atMostOnce()).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testGetRestaurantDishes() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);
        Dish dish = getDefaultDish(restaurant);

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, atMostOnce()).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testCreateRestaurantDish() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);
        Dish dish = getDefaultDish(restaurant);

        CreateDishRequest createDishRequest = new CreateDishRequest("Pizza Salami", BigDecimal.valueOf(7.5));

        when(restaurantService.validateAndGetRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(dishService.saveDish(any(Dish.class))).thenReturn(dish);
        when(restaurantService.saveRestaurant(any(Restaurant.class))).thenReturn(restaurant);

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- create dish and put {restaurantI,dishId} in DISHES
        //-- evict restaurantId of DISHES
        //-- evict restaurantId of RESTAURANTS
        mockMvc.perform(post(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())
                        .contentType((MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(createDishRequest)))
                .andExpect(status().isCreated());

        //-- {restaurantId,dishId} already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(5)).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testUpdateRestaurantDish() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);
        Dish dish = getDefaultDish(restaurant);

        UpdateDishRequest updateDishRequest = new UpdateDishRequest("Pizza Pepperoni", BigDecimal.valueOf(6.5));

        when(dishService.saveDish(any(Dish.class))).thenReturn(dish);
        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);
        when(restaurantService.validateAndGetDish(any(Restaurant.class), any(UUID.class))).thenReturn(dish);
        when(restaurantService.saveRestaurant(any(Restaurant.class))).thenReturn(restaurant);

        //-- {restaurantId,dishId} cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- {restaurantI,dishId} updated in DISHES
        //-- evict restaurantId of DISHES
        //-- evict restaurantId of RESTAURANTS
        mockMvc.perform(put(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId())
                        .contentType((MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(updateDishRequest)))
                .andExpect(status().isOk());

        //-- {restaurantId,dishId} already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(6)).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testDeleteRestaurantDish() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);
        Dish dish = getDefaultDish(restaurant);

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);
        when(restaurantService.validateAndGetDish(any(Restaurant.class), any(UUID.class))).thenReturn(dish);

        //-- {restaurantId,dishId} cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- evict {restaurantI,dishId} of DISHES
        //-- evict restaurantId of DISHES
        //-- evict restaurantId of RESTAURANTS
        mockMvc.perform(delete(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        //-- {restaurantId,dishId} cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId())).andExpect(status().isOk());

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- {restaurantId,dishId} already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(7)).validateAndGetRestaurant(restaurant.getId());
    }

    private Dish getDefaultDish(Restaurant restaurant) {
        Dish dish = new Dish("Pizza Salami", BigDecimal.valueOf(7.5));
        dish.setId(UUID.fromString("f3e53136-4c34-484b-8d9e-128264707c66"));
        restaurant.getDishes().add(dish);
        return dish;
    }

    private City getDefaultCity() {
        City city = new City("Porto");
        city.setId(UUID.fromString("c0b8602c-225e-4995-8724-035c504f8c84"));
        return city;
    }

    private Restaurant getDefaultRestaurant(City city) {
        Restaurant restaurant = new Restaurant("Happy Pizza", city);
        restaurant.setId(UUID.fromString("7ee00128-6f10-49ae-9edf-72495e77adf6"));
        return restaurant;
    }

    private static final String API_RESTAURANTS_RESTAURANT_ID_URL = "/api/restaurants/{restaurantId}";
    private static final String API_RESTAURANTS_RESTAURANT_ID_DISHES_URL = "/api/restaurants/{restaurantId}/dishes";
    private static final String API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL = "/api/restaurants/{restaurantId}/dishes/{dishId}";
}