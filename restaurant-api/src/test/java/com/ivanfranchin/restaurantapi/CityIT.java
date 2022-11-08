package com.ivanfranchin.restaurantapi;

import com.ivanfranchin.restaurantapi.model.City;
import com.ivanfranchin.restaurantapi.repository.CityRepository;
import com.ivanfranchin.restaurantapi.rest.dto.CityResponse;
import com.ivanfranchin.restaurantapi.rest.dto.CreateCityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CityIT extends AbstractTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
    }

    @Test
    void testGetCity() {
        City city = saveCity("Porto");

        String url = String.format(API_CITIES_CITY_ID_URL, city.getId());
        ResponseEntity<CityResponse> responseEntity = testRestTemplate.getForEntity(url, CityResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isNotNull();
        assertThat(responseEntity.getBody().name()).isEqualTo(city.getName());
        assertThat(responseEntity.getBody().restaurants().size()).isEqualTo(0);
    }

    @Test
    void testGetCities() {
        ParameterizedTypeReference<RestResponsePageImpl<City>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<City>> responseEntity = testRestTemplate.exchange(
                API_CITIES_URL, HttpMethod.GET, null, responseType);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(0);
        assertThat(responseEntity.getBody().getContent().size()).isEqualTo(0);
    }

    @Test
    void testCreateCity() {
        CreateCityRequest createCityRequest = new CreateCityRequest("Porto");

        ResponseEntity<CityResponse> responseEntity = testRestTemplate.postForEntity(
                API_CITIES_URL, createCityRequest, CityResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isNotNull();
        assertThat(responseEntity.getBody().name()).isEqualTo(createCityRequest.getName());
        assertThat(responseEntity.getBody().restaurants().size()).isEqualTo(0);

        Optional<City> optionalCity = cityRepository.findById(responseEntity.getBody().id());
        assertThat(optionalCity.isPresent()).isTrue();
    }

    @Test
    void testDeleteCity() {
        City city = saveCity("Porto");

        String url = String.format(API_CITIES_CITY_ID_URL, city.getId());
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertThat(optionalCity.isPresent()).isFalse();
    }

    private City saveCity(String name) {
        return cityRepository.save(new City(name));
    }

    private static final String API_CITIES_URL = "/api/cities";
    private static final String API_CITIES_CITY_ID_URL = "/api/cities/%s";
}