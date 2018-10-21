package com.mycompany.springbootneo4jcaffeine.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.springbootneo4jcaffeine.config.CacheConfig;
import com.mycompany.springbootneo4jcaffeine.config.MapperConfig;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RestaurantController.class)
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

    @Test
    void testGetRestaurantCaching() throws Exception {
        Restaurant restaurant = getDefaultRestaurant();
        given(restaurantService.validateAndGetRestaurantById(restaurant.getId())).willReturn(restaurant);

        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurant.getId())).andExpect(status().isOk());

        verify(restaurantService, times(1)).validateAndGetRestaurantById(restaurant.getId());
    }

    @Test
    void testCreateRestaurantCaching() {
    }

    @Test
    void testUpdateRestaurantCaching() {
    }

    @Test
    void testDeleteRestaurantCaching() {
    }

    private Restaurant getDefaultRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId("7ee00128-6f10-49ae-9edf-72495e77adf6");
        restaurant.setName("Happy Pizza");
        return restaurant;
    }
}