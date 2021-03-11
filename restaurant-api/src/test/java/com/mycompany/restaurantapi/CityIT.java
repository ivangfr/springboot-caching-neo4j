package com.mycompany.restaurantapi;

import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.repository.CityRepository;
import com.mycompany.restaurantapi.rest.dto.CityDto;
import com.mycompany.restaurantapi.rest.dto.CreateCityDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CityIT extends AbstractTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void testGetCity() {
        City city = saveDefaultCity();

        String url = String.format(API_CITIES_CITY_ID_URL, city.getId());
        ResponseEntity<CityDto> responseEntity = testRestTemplate.getForEntity(url, CityDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getId());
        assertEquals(city.getName(), responseEntity.getBody().getName());
        assertEquals(0, responseEntity.getBody().getRestaurants().size());
    }

    @Test
    void testCreateCity() {
        CreateCityDto createCityDto = getDefaultCreateCityDto();

        ResponseEntity<CityDto> responseEntity = testRestTemplate.postForEntity(API_CITIES_URL, createCityDto, CityDto.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getId());
        assertEquals(createCityDto.getName(), responseEntity.getBody().getName());
        assertEquals(0, responseEntity.getBody().getRestaurants().size());

        Optional<City> optionalCity = cityRepository.findById(responseEntity.getBody().getId());
        assertTrue(optionalCity.isPresent());
    }

    @Test
    void testDeleteCity() {
        City city = saveDefaultCity();

        String url = String.format(API_CITIES_CITY_ID_URL, city.getId());
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertFalse(optionalCity.isPresent());
    }

    private CreateCityDto getDefaultCreateCityDto() {
        CreateCityDto createCityDto = new CreateCityDto();
        createCityDto.setName("Porto");
        return createCityDto;
    }

    private City saveDefaultCity() {
        City city = new City();
        city.setName("Porto");
        return cityRepository.save(city);
    }

    private static final String API_CITIES_URL = "/api/cities";
    private static final String API_CITIES_CITY_ID_URL = "/api/cities/%s";
}