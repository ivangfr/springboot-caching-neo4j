package com.mycompany.restaurantapi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.restaurantapi.mapper.DishMapperImpl;
import com.mycompany.restaurantapi.mapper.RestaurantMapperImpl;
import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.model.Dish;
import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.rest.dto.CreateDishRequest;
import com.mycompany.restaurantapi.rest.dto.UpdateDishRequest;
import com.mycompany.restaurantapi.service.CityService;
import com.mycompany.restaurantapi.service.DishService;
import com.mycompany.restaurantapi.service.RestaurantService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

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
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {RestaurantDishController.class, RestaurantController.class})
@Import({RestaurantMapperImpl.class, DishMapperImpl.class, CachingTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantDishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private DishService dishService;

    private static City city;
    private static Restaurant restaurant;

    @BeforeAll
    static void setUp() {
        city = getDefaultCity();
        restaurant = getDefaultRestaurant();
    }

    @Test
    void testGetRestaurantDish() throws Exception {
        Dish dish = getDefaultDish();
        restaurant.getDishes().add(dish);

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
        Dish dish = getDefaultDish();
        restaurant.getDishes().add(dish);

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, atMostOnce()).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testCreateRestaurantDish() throws Exception {
        Dish dish = getDefaultDish();
        restaurant.getDishes().add(dish);
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
        Dish dish = getDefaultDish();
        restaurant.getDishes().add(dish);
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
        Dish dish = getDefaultDish();
        restaurant.getDishes().add(dish);

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

    private Dish getDefaultDish() {
        Dish dish = new Dish();
        dish.setId(UUID.fromString("f3e53136-4c34-484b-8d9e-128264707c66"));
        dish.setName("Pizza Salami");
        dish.setPrice(BigDecimal.valueOf(7.5));
        return dish;
    }

    private static City getDefaultCity() {
        City city = new City();
        city.setId(UUID.fromString("c0b8602c-225e-4995-8724-035c504f8c84"));
        city.setName("Porto");
        return city;
    }

    private static Restaurant getDefaultRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(UUID.fromString("7ee00128-6f10-49ae-9edf-72495e77adf6"));
        restaurant.setName("Happy Pizza");
        restaurant.setCity(city);
        return restaurant;
    }

    private static final String API_RESTAURANTS_RESTAURANT_ID_URL = "/api/restaurants/{restaurantId}";
    private static final String API_RESTAURANTS_RESTAURANT_ID_DISHES_URL = "/api/restaurants/{restaurantId}/dishes";
    private static final String API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL = "/api/restaurants/{restaurantId}/dishes/{dishId}";
}