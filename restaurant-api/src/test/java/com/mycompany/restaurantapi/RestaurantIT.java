package com.mycompany.restaurantapi;

import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.repository.CityRepository;
import com.mycompany.restaurantapi.repository.RestaurantRepository;
import com.mycompany.restaurantapi.rest.dto.CreateRestaurantRequest;
import com.mycompany.restaurantapi.rest.dto.RestaurantResponse;
import com.mycompany.restaurantapi.rest.dto.UpdateRestaurantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantIT extends AbstractTestcontainers {

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
        city = saveDefaultCity("Porto");
        city2 = saveDefaultCity("Berlin");
    }

    @Test
    void testGetRestaurant() {
        Restaurant restaurant = saveDefaultRestaurant();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId());
        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.getForEntity(url, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isEqualTo(restaurant.getId());
        assertThat(responseEntity.getBody().getName()).isEqualTo(restaurant.getName());
        assertThat(responseEntity.getBody().getCity().getId()).isEqualTo(restaurant.getCity().getId());
        assertThat(responseEntity.getBody().getCity().getName()).isEqualTo(restaurant.getCity().getName());
        assertThat(responseEntity.getBody().getDishes().size()).isEqualTo(0);
    }

    @Test
    void testGetRestaurants() {
        ParameterizedTypeReference<RestResponsePageImpl<Restaurant>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<Restaurant>> responseEntity = testRestTemplate.exchange(
                API_RESTAURANTS_URL, HttpMethod.GET, null, responseType);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        // @DirtiesContext is not working
        // --
        // assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(0);
        // assertThat(responseEntity.getBody().getContent().size()).isEqualTo(0);
    }

    @Test
    void testCreateRestaurant() {
        CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(city.getId(), "Happy Pizza");

        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.postForEntity(
                API_RESTAURANTS_URL, createRestaurantRequest, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isNotNull();
        assertThat(responseEntity.getBody().getName()).isEqualTo(createRestaurantRequest.getName());
        assertThat(responseEntity.getBody().getCity().getId()).isEqualTo(createRestaurantRequest.getCityId());
        assertThat(responseEntity.getBody().getDishes().size()).isEqualTo(0);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(responseEntity.getBody().getId());
        assertThat(optionalRestaurant.isPresent()).isTrue();
        optionalRestaurant.ifPresent(r -> assertThat(r.getCity()).isNotNull());

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertThat(optionalCity.isPresent()).isTrue();
        optionalCity.ifPresent(c -> assertThat(c.getRestaurants().size()).isEqualTo(1));
    }

    @Test
    void testUpdateRestaurant() {
        Restaurant restaurant = saveDefaultRestaurant();

        UpdateRestaurantRequest updateRestaurantRequest = new UpdateRestaurantRequest(city2.getId(), "Happy Burger");

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId());
        HttpEntity<UpdateRestaurantRequest> requestUpdate = new HttpEntity<>(updateRestaurantRequest);
        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.exchange(
                url, HttpMethod.PUT, requestUpdate, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isEqualTo(restaurant.getId());
        assertThat(responseEntity.getBody().getName()).isEqualTo(updateRestaurantRequest.getName());
        assertThat(responseEntity.getBody().getCity().getId()).isEqualTo(updateRestaurantRequest.getCityId());
        assertThat(responseEntity.getBody().getDishes().size()).isEqualTo(0);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertThat(optionalRestaurant.isPresent()).isTrue();
        optionalRestaurant.ifPresent(r -> assertThat(r.getCity()).isNotNull());

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertThat(optionalCity.isPresent()).isTrue();
        optionalCity.ifPresent(c -> assertThat(c.getRestaurants().size()).isEqualTo(0));

        Optional<City> optionalCity2 = cityRepository.findById(city2.getId());
        assertThat(optionalCity2.isPresent()).isTrue();
        optionalCity2.ifPresent(c -> assertThat(c.getRestaurants().size()).isEqualTo(1));
    }

    @Test
    void testDeleteRestaurant() {
        Restaurant restaurant = saveDefaultRestaurant();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId());
        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.exchange(
                url, HttpMethod.DELETE, null, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isEqualTo(restaurant.getId());
        assertThat(responseEntity.getBody().getName()).isEqualTo(restaurant.getName());
        assertThat(responseEntity.getBody().getCity().getId()).isEqualTo(restaurant.getCity().getId());
        assertThat(responseEntity.getBody().getCity().getName()).isEqualTo(restaurant.getCity().getName());
        assertThat(responseEntity.getBody().getDishes().size()).isEqualTo(0);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertThat(optionalRestaurant.isPresent()).isFalse();

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertThat(optionalCity.isPresent()).isTrue();
        optionalCity.ifPresent(c -> assertThat(c.getRestaurants().size()).isEqualTo(0));
    }

    private City saveDefaultCity(String name) {
        City city = new City();
        city.setName(name);
        return cityRepository.save(city);
    }

    private Restaurant saveDefaultRestaurant() {
        Restaurant defaultRestaurant = new Restaurant();
        defaultRestaurant.setName("Happy Pizza");
        defaultRestaurant.setCity(city);

        city.getRestaurants().add(defaultRestaurant);
        cityRepository.save(city);

        return restaurantRepository.save(defaultRestaurant);
    }

    private static final String API_RESTAURANTS_URL = "/api/restaurants";
    private static final String API_RESTAURANTS_RESTAURANT_ID_URL = "/api/restaurants/%s";
}