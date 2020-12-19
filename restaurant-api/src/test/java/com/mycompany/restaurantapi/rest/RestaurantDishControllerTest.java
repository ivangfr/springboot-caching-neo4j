package com.mycompany.restaurantapi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.restaurantapi.config.CacheConfig;
import com.mycompany.restaurantapi.mapper.DishMapperImpl;
import com.mycompany.restaurantapi.mapper.RestaurantMapperImpl;
import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.model.Dish;
import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.rest.dto.CreateDishDto;
import com.mycompany.restaurantapi.rest.dto.UpdateDishDto;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// The @AutoConfigureDataNeo4j annotation is used instead of @DataNeo4jTest because both @DataNeo4jTest and @WebMvcTest
// define a @BootstrapWith annotation and having two @BootstrapWith annotations in a test class is not supported.
@AutoConfigureDataNeo4j
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {RestaurantDishController.class, RestaurantController.class})
@Import({RestaurantMapperImpl.class, DishMapperImpl.class, CacheConfig.class})
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

        given(restaurantService.validateAndGetRestaurant(restaurant.getId())).willReturn(restaurant);
        given(restaurantService.validateAndGetDish(restaurant, dish.getId())).willReturn(dish);

        //-- {restaurantId,dishId} cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        //-- {restaurantId,dishId} already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId()))
                .andExpect(status().isOk());

        verify(restaurantService, times(1)).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testGetRestaurantDishes() throws Exception {
        Dish dish = getDefaultDish();
        restaurant.getDishes().add(dish);

        given(restaurantService.validateAndGetRestaurant(restaurant.getId())).willReturn(restaurant);

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(1)).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testCreateRestaurantDish() throws Exception {
        Dish dish = getDefaultDish();
        restaurant.getDishes().add(dish);
        CreateDishDto createDishDto = getDefaultCreateDishDto();

        given(restaurantService.validateAndGetRestaurant(restaurant.getId())).willReturn(restaurant);
        given(dishService.saveDish(any(Dish.class))).willReturn(dish);
        given(restaurantService.saveRestaurant(any(Restaurant.class))).willReturn(restaurant);

        //-- restaurantId cached in DISHES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- create dish and put {restaurantI,dishId} in DISHES
        //-- evict restaurantId of DISHES
        //-- evict restaurantId of RESTAURANTS
        mockMvc.perform(post(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId())
                .contentType((MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(createDishDto)))
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
        UpdateDishDto updateDishDto = getDefaultUpdateDishDto();

        given(dishService.saveDish(any(Dish.class))).willReturn(dish);
        given(restaurantService.validateAndGetRestaurant(restaurant.getId())).willReturn(restaurant);
        given(restaurantService.validateAndGetDish(restaurant, dish.getId())).willReturn(dish);
        given(restaurantService.saveRestaurant(any(Restaurant.class))).willReturn(restaurant);

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
                .content(objectMapper.writeValueAsString(updateDishDto)))
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

        given(restaurantService.validateAndGetRestaurant(restaurant.getId())).willReturn(restaurant);
        given(restaurantService.validateAndGetDish(restaurant, dish.getId())).willReturn(dish);

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

    private CreateDishDto getDefaultCreateDishDto() {
        CreateDishDto createDishDto = new CreateDishDto();
        createDishDto.setName("Pizza Salami");
        createDishDto.setPrice(BigDecimal.valueOf(7.5));
        return createDishDto;
    }

    private UpdateDishDto getDefaultUpdateDishDto() {
        UpdateDishDto updateDishDto = new UpdateDishDto();
        updateDishDto.setName("Pizza Pepperoni");
        updateDishDto.setPrice(BigDecimal.valueOf(6.5));
        return updateDishDto;
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