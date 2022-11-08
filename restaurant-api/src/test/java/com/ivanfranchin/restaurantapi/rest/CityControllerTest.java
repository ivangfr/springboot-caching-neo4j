package com.ivanfranchin.restaurantapi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanfranchin.restaurantapi.mapper.CityMapperImpl;
import com.ivanfranchin.restaurantapi.model.City;
import com.ivanfranchin.restaurantapi.rest.dto.CreateCityRequest;
import com.ivanfranchin.restaurantapi.service.CityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.ivanfranchin.restaurantapi.config.CachingConfig.CITIES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisabledIf("#{environment.acceptsProfiles('redis')}")
@AutoConfigureDataNeo4j /* The @AutoConfigureDataNeo4j annotation is used instead of @DataNeo4jTest because both
                           @DataNeo4jTest and @WebMvcTest set @BootstrapWith annotation and having two @BootstrapWith
                           annotations in a test class is not supported. */
@WebMvcTest(CityController.class)
@Import({CityMapperImpl.class, CachingTestConfig.class})
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    @BeforeEach
    void setUp() {
        cacheManager.getCache(CITIES).clear();
    }

    @Test
    void testGetCityCaching() throws Exception {
        City city = getDefaultCity();
        when(cityService.validateAndGetCity(any(UUID.class))).thenReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        verify(cityService, atMostOnce()).validateAndGetCity(city.getId());
    }

    @Test
    void testCreateCityCaching() throws Exception {
        City city = getDefaultCity();
        CreateCityRequest createCityRequest = new CreateCityRequest("Porto");

        when(cityService.validateAndGetCity(any(UUID.class))).thenReturn(city);
        when(cityService.saveCity(any(City.class))).thenReturn(city);

        //-- create city and put cityId in CITIES
        mockMvc.perform(post(API_CITIES_URL)
                        .contentType((MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(createCityRequest)))
                .andExpect(status().isCreated());

        //-- cityId already cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        verify(cityService, never()).validateAndGetCity(city.getId());
    }

    @Test
    void testDeleteCityCaching() throws Exception {
        City city = getDefaultCity();
        when(cityService.validateAndGetCity(any(UUID.class))).thenReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- evict cityId of CITIES
        mockMvc.perform(delete(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get(API_CITIES_CITY_ID_URL, city.getId())).andExpect(status().isOk());

        verify(cityService, times(3)).validateAndGetCity(city.getId());
    }

    private City getDefaultCity() {
        City city = new City();
        city.setId(UUID.fromString("c0b8602c-225e-4995-8724-035c504f8c84"));
        city.setName("Porto");
        return city;
    }

    private static final String API_CITIES_URL = "/api/cities";
    private static final String API_CITIES_CITY_ID_URL = "/api/cities/{cityId}";
}