package com.mycompany.springbootneo4jcaffeine;

import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.model.Restaurant;
import com.mycompany.springbootneo4jcaffeine.repository.CityRepository;
import com.mycompany.springbootneo4jcaffeine.repository.RestaurantRepository;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateRestaurantDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.RestaurantDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.UpdateRestaurantDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private City city;
    private City city2;

    @BeforeEach
    void setUp() {
        city = saveDefaultCity();
        city2 = saveDefaultCity2();
    }

    @Test
    void testGetRestaurant() {
        Restaurant restaurant = saveDefaultRestaurant();

        String url = String.format("/api/restaurants/%s", restaurant.getId());
        ResponseEntity<RestaurantDto> responseEntity = testRestTemplate.getForEntity(url, RestaurantDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(restaurant.getId(), responseEntity.getBody().getId());
        assertEquals(restaurant.getName(), responseEntity.getBody().getName());
        assertEquals(restaurant.getCity().getId(), responseEntity.getBody().getCity().getId());
        assertEquals(restaurant.getCity().getName(), responseEntity.getBody().getCity().getName());
        assertEquals(0, responseEntity.getBody().getDishes().size());
    }

    @Test
    void testCreateRestaurant() {
        CreateRestaurantDto createRestaurantDto = getDefaultCreateRestaurantDto();

        ResponseEntity<RestaurantDto> responseEntity = testRestTemplate.postForEntity("/api/restaurants", createRestaurantDto, RestaurantDto.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getId());
        assertEquals(createRestaurantDto.getName(), responseEntity.getBody().getName());
        assertEquals(createRestaurantDto.getCityId(), responseEntity.getBody().getCity().getId());
        assertEquals(0, responseEntity.getBody().getDishes().size());

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertTrue(optionalCity.isPresent());
        assertEquals(1, optionalCity.get().getRestaurants().size());
    }

    @Test
    void testUpdateRestaurant() {
        Restaurant restaurant = saveDefaultRestaurant();

        UpdateRestaurantDto updateRestaurantDto = getDefaultUpdateRestaurantDto();

        String url = String.format("/api/restaurants/%s", restaurant.getId());
        HttpEntity<UpdateRestaurantDto> requestUpdate = new HttpEntity<>(updateRestaurantDto);
        ResponseEntity<RestaurantDto> responseEntity = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, RestaurantDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(restaurant.getId(), responseEntity.getBody().getId());
        assertEquals(updateRestaurantDto.getName(), responseEntity.getBody().getName());
        assertEquals(updateRestaurantDto.getCityId(), responseEntity.getBody().getCity().getId());
        assertEquals(0, responseEntity.getBody().getDishes().size());

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertTrue(optionalCity.isPresent());
        assertEquals(0, optionalCity.get().getRestaurants().size());

        Optional<City> optionalCity2 = cityRepository.findById(city2.getId());
        assertTrue(optionalCity2.isPresent());
        assertEquals(1, optionalCity2.get().getRestaurants().size());
    }

    @Test
    void testDeleteRestaurant() {
        Restaurant restaurant = saveDefaultRestaurant();

        String url = String.format("/api/restaurants/%s", restaurant.getId());
        ResponseEntity<RestaurantDto> responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, null, RestaurantDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(restaurant.getId(), responseEntity.getBody().getId());
        assertEquals(restaurant.getName(), responseEntity.getBody().getName());
        assertEquals(restaurant.getCity().getId(), responseEntity.getBody().getCity().getId());
        assertEquals(restaurant.getCity().getName(), responseEntity.getBody().getCity().getName());
        assertEquals(0, responseEntity.getBody().getDishes().size());

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertTrue(optionalCity.isPresent());
        assertEquals(0, optionalCity.get().getRestaurants().size());
    }

    private CreateRestaurantDto getDefaultCreateRestaurantDto() {
        CreateRestaurantDto createRestaurantDto = new CreateRestaurantDto();
        createRestaurantDto.setName("Happy Pizza");
        createRestaurantDto.setCityId(city.getId());
        return createRestaurantDto;
    }

    private UpdateRestaurantDto getDefaultUpdateRestaurantDto() {
        UpdateRestaurantDto updateRestaurantDto = new UpdateRestaurantDto();
        updateRestaurantDto.setName("Happy Burger");
        updateRestaurantDto.setCityId(city2.getId());
        return updateRestaurantDto;
    }

    private City saveDefaultCity() {
        City city = new City();
        city.setName("Porto");
        return cityRepository.save(city);
    }

    private City saveDefaultCity2() {
        City city = new City();
        city.setName("Berlin");
        return cityRepository.save(city);
    }

    private Restaurant saveDefaultRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Happy Pizza");
        restaurant.setCity(city);

        city.getRestaurants().add(restaurant);
        cityRepository.save(city);

        return restaurantRepository.save(restaurant);
    }

}