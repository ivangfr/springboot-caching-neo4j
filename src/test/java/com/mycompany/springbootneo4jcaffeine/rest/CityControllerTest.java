package com.mycompany.springbootneo4jcaffeine.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.springbootneo4jcaffeine.config.CacheConfig;
import com.mycompany.springbootneo4jcaffeine.config.MapperConfig;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateCityDto;
import com.mycompany.springbootneo4jcaffeine.service.CityService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CityController.class)
@Import({MapperConfig.class, CacheConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    @TestConfiguration
    static class Neo4jConfig {

        @Bean
        SessionFactory sessionFactory() {
            return new SessionFactory("com.mycompany.springbootneo4jcaffeine.model");
        }
    }

    @Test
    void testGetCityCaching() throws Exception {
        City city = getDefaultCity();
        given(cityService.validateAndGetCity(city.getId())).willReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        verify(cityService, times(1)).validateAndGetCity(city.getId());
    }

    @Test
    void testCreateCityCaching() throws Exception {
        City city = getDefaultCity();
        CreateCityDto createCityDto = getDefaultCreateCityDto();

        given(cityService.validateAndGetCity(city.getId())).willReturn(city);
        given(cityService.saveCity(any(City.class))).willReturn(city);

        //-- create city and put cityId in CITIES
        mockMvc.perform(post("/api/cities")
                .contentType((MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(createCityDto)))
                .andExpect(status().isCreated());

        //-- cityId already cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        verify(cityService, times(0)).validateAndGetCity(city.getId());
    }

    @Test
    void testDeleteCityCaching() throws Exception {
        City city = getDefaultCity();

        given(cityService.validateAndGetCity(city.getId())).willReturn(city);

        //-- cityId cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        //-- evict cityId of CITIES
        mockMvc.perform(delete("/api/cities/{id}", city.getId())).andExpect(status().isOk());

        //-- cityId cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        //-- cityId already cached in CITIES
        mockMvc.perform(get("/api/cities/{cityId}", city.getId())).andExpect(status().isOk());

        verify(cityService, times(3)).validateAndGetCity(city.getId());
    }

    private City getDefaultCity() {
        City city = new City();
        city.setId("c0b8602c-225e-4995-8724-035c504f8c84");
        city.setName("Porto");
        return city;
    }

    private CreateCityDto getDefaultCreateCityDto() {
        CreateCityDto createCityDto = new CreateCityDto();
        createCityDto.setName("Porto");
        return createCityDto;
    }
}