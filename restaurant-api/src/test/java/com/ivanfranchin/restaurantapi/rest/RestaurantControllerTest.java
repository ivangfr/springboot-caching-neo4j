package com.ivanfranchin.restaurantapi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanfranchin.restaurantapi.mapper.CityMapperImpl;
import com.ivanfranchin.restaurantapi.mapper.RestaurantMapperImpl;
import com.ivanfranchin.restaurantapi.model.City;
import com.ivanfranchin.restaurantapi.model.Restaurant;
import com.ivanfranchin.restaurantapi.rest.dto.CreateRestaurantRequest;
import com.ivanfranchin.restaurantapi.rest.dto.UpdateRestaurantRequest;
import com.ivanfranchin.restaurantapi.service.CityService;
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

import java.util.UUID;

import static com.ivanfranchin.restaurantapi.config.CachingConfig.CITIES;
import static com.ivanfranchin.restaurantapi.config.CachingConfig.RESTAURANTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
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
@WebMvcTest(controllers = {RestaurantController.class, CityController.class})
@Import({CityMapperImpl.class, RestaurantMapperImpl.class, CachingTestConfig.class})
class RestaurantControllerTest {

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

    @BeforeEach
    void setUp() {
        cacheManager.getCache(RESTAURANTS).clear();
        cacheManager.getCache(CITIES).clear();
    }

    @Test
    void testGetRestaurant() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);

        //-- restaurantId cached in CITIES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in CITIES
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, atMostOnce()).validateAndGetRestaurant(restaurant.getId());
    }

    @Test
    void testCreateRestaurant() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);

        CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(city.getId(), "Happy Pizza");

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);
        when(restaurantService.saveRestaurant(any(Restaurant.class))).thenReturn(restaurant);
        when(cityService.validateAndGetCity(any(UUID.class))).thenReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- create restaurant and put restaurantId in RESTAURANTS
        //-- evict cityId of CITIES
        mockMvc.perform(post(API_RESTAURANTS_URL)
                        .contentType((MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(createRestaurantRequest)))
                .andExpect(status().isCreated());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        verify(restaurantService, never()).validateAndGetRestaurant(restaurant.getId());
        verify(cityService, times(3)).validateAndGetCity(city.getId());
    }

    @Test
    void testUpdateRestaurant() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);

        UpdateRestaurantRequest updateRestaurantRequest = new UpdateRestaurantRequest(city.getId(), "Happy Burger");

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);
        when(restaurantService.saveRestaurant(any(Restaurant.class))).thenReturn(restaurant);
        when(cityService.validateAndGetCity(any(UUID.class))).thenReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- restaurantId updated in RESTAURANTS
        //-- evict cityId of CITIES
        mockMvc.perform(put(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())
                        .contentType((MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(updateRestaurantRequest)))
                .andExpect(status().isOk());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        verify(restaurantService, atMostOnce()).validateAndGetRestaurant(restaurant.getId());
        verify(cityService, times(3)).validateAndGetCity(city.getId());
    }

    @Test
    void testDeleteRestaurant() throws Exception {
        City city = getDefaultCity();
        Restaurant restaurant = getDefaultRestaurant(city);

        when(restaurantService.validateAndGetRestaurant(any(UUID.class))).thenReturn(restaurant);
        when(cityService.validateAndGetCity(any(UUID.class))).thenReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- evict restaurantId of RESTAURANTS
        //-- evict cityId of CITIES
        mockMvc.perform(delete(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(3)).validateAndGetRestaurant(restaurant.getId());
        verify(cityService, times(2)).validateAndGetCity(city.getId());
    }

    private Restaurant getDefaultRestaurant(City city) {
        Restaurant restaurant = new Restaurant("Happy Pizza", city);
        restaurant.setId(UUID.fromString("7ee00128-6f10-49ae-9edf-72495e77adf6"));
        return restaurant;
    }

    private City getDefaultCity() {
        City city = new City("Porto");
        city.setId(UUID.fromString("c0b8602c-225e-4995-8724-035c504f8c84"));
        return city;
    }

    private static final String API_CITIES_CITY_ID_URL = "/api/cities/{cityId}";
    private static final String API_RESTAURANTS_URL = "/api/restaurants";
    private static final String API_RESTAURANTS_RESTAURANT_ID_URL = "/api/restaurants/{restaurantId}";
}