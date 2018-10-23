package com.mycompany.springbootneo4jcaffeine.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.springbootneo4jcaffeine.config.CacheConfig;
import com.mycompany.springbootneo4jcaffeine.config.MapperConfig;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.UpdateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.service.CityService;
import com.mycompany.springbootneo4jcaffeine.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {RestaurantController.class, CityController.class})
@Import({MapperConfig.class, CacheConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    @MockBean
    private RestaurantService restaurantService;

    @TestConfiguration
    static class Neo4jConfig {

        @Bean
        SessionFactory sessionFactory() {
            return new SessionFactory("com.mycompany.springbootneo4jcaffeine.model");
        }
    }

    private City city = getDefaultCity();

    @Test
    void testGetRestaurantCaching() throws Exception {
        Restaurant restaurant = getDefaultRestaurant();
        given(restaurantService.validateAndGetRestaurantById(restaurant.getId())).willReturn(restaurant);

        //-- restaurantId cached in CITIES
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        //-- restaurantId already cached in CITIES
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(1)).validateAndGetRestaurantById(restaurant.getId());
    }

    @Test
    void testCreateRestaurantCaching() throws Exception {
        Restaurant restaurant = getDefaultRestaurant();
        CreateRestaurantDto createRestaurantDto = getDefaultCreateRestaurantDto();

        given(restaurantService.validateAndGetRestaurantById(restaurant.getId())).willReturn(restaurant);
        given(restaurantService.saveRestaurant(any(Restaurant.class))).willReturn(restaurant);
        given(cityService.validateAndGetCityById(city.getId())).willReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        //-- create restaurant and put restaurantId in RESTAURANTS
        //-- evict cityId of CITIES
        mockMvc.perform(post("/api/restaurants")
                .contentType((MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(createRestaurantDto)))
                .andExpect(status().isCreated());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        verify(restaurantService, times(0)).validateAndGetRestaurantById(restaurant.getId());
        verify(cityService, times(3)).validateAndGetCityById(city.getId());
    }

    @Test
    void testUpdateRestaurantCaching() throws Exception {
        Restaurant restaurant = getDefaultRestaurant();
        UpdateRestaurantDto updateRestaurantDto = getDefaultUpdateRestaurantDto();

        given(restaurantService.validateAndGetRestaurantById(restaurant.getId())).willReturn(restaurant);
        given(restaurantService.saveRestaurant(any(Restaurant.class))).willReturn(restaurant);
        given(cityService.validateAndGetCityById(city.getId())).willReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        //-- restaurantId updated in RESTAURANTS
        //-- evict cityId of CITIES
        mockMvc.perform(patch("/api/restaurants/{restaurantId}", restaurant.getId())
                .contentType((MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(updateRestaurantDto)))
                .andExpect(status().isOk());

        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        verify(restaurantService, times(1)).validateAndGetRestaurantById(restaurant.getId());
        verify(cityService, times(3)).validateAndGetCityById(city.getId());
    }

    @Test
    void testDeleteRestaurantCaching() throws Exception {
        Restaurant restaurant = getDefaultRestaurant();

        given(restaurantService.validateAndGetRestaurantById(restaurant.getId())).willReturn(restaurant);
        given(cityService.validateAndGetCityById(city.getId())).willReturn(city);

        //-- cityId cached in CITIES
        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        //-- evict restaurantId of RESTAURANTS
        //-- evict cityId of CITIES
        mockMvc.perform(delete("/api/restaurants/{id}", restaurant.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        //-- restaurantId cached in RESTAURANTS
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        //-- restaurantId already cached in RESTAURANTS
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(3)).validateAndGetRestaurantById(restaurant.getId());
        verify(cityService, times(2)).validateAndGetCityById(city.getId());
    }

    private Restaurant getDefaultRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId("7ee00128-6f10-49ae-9edf-72495e77adf6");
        restaurant.setName("Happy Pizza");
        restaurant.setCity(city);
        return restaurant;
    }

    private CreateRestaurantDto getDefaultCreateRestaurantDto() {
        CreateRestaurantDto createRestaurantDto = new CreateRestaurantDto();
        createRestaurantDto.setCityId(city.getId());
        createRestaurantDto.setName("Happy Pizza");
        return createRestaurantDto;
    }

    private UpdateRestaurantDto getDefaultUpdateRestaurantDto() {
        UpdateRestaurantDto updateRestaurantDto = new UpdateRestaurantDto();
        updateRestaurantDto.setCityId(city.getId());
        updateRestaurantDto.setName("Happy Burger");
        return updateRestaurantDto;
    }

    private City getDefaultCity() {
        City city = new City();
        city.setId("c0b8602c-225e-4995-8724-035c504f8c84");
        city.setName("Porto");
        return city;
    }

}