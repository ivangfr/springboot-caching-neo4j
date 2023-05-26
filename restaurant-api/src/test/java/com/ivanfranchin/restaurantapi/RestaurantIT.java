package com.ivanfranchin.restaurantapi;

import com.ivanfranchin.restaurantapi.model.City;
import com.ivanfranchin.restaurantapi.model.Restaurant;
import com.ivanfranchin.restaurantapi.repository.CityRepository;
import com.ivanfranchin.restaurantapi.repository.RestaurantRepository;
import com.ivanfranchin.restaurantapi.rest.dto.CreateRestaurantRequest;
import com.ivanfranchin.restaurantapi.rest.dto.RestaurantResponse;
import com.ivanfranchin.restaurantapi.rest.dto.UpdateRestaurantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportTestcontainers(MyContainers.class)
class RestaurantIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        cityRepository.deleteAll();
    }

    @Test
    void testGetRestaurant() {
        City city = saveCity("Porto");
        Restaurant restaurant = saveRestaurant(city);

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId());
        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.getForEntity(url, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(restaurant.getId());
        assertThat(responseEntity.getBody().name()).isEqualTo(restaurant.getName());
        assertThat(responseEntity.getBody().city().id()).isEqualTo(restaurant.getCity().getId());
        assertThat(responseEntity.getBody().city().name()).isEqualTo(restaurant.getCity().getName());
        assertThat(responseEntity.getBody().dishes().size()).isEqualTo(0);
    }

    @Test
    void testGetRestaurants() {
        ParameterizedTypeReference<RestResponsePageImpl<Restaurant>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<Restaurant>> responseEntity = testRestTemplate.exchange(
                API_RESTAURANTS_URL, HttpMethod.GET, null, responseType);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(0);
        assertThat(responseEntity.getBody().getContent().size()).isEqualTo(0);
    }

    @Test
    void testCreateRestaurant() {
        City city = saveCity("Porto");
        CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(city.getId(), "Happy Pizza");

        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.postForEntity(
                API_RESTAURANTS_URL, createRestaurantRequest, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isNotNull();
        assertThat(responseEntity.getBody().name()).isEqualTo(createRestaurantRequest.getName());
        assertThat(responseEntity.getBody().city().id()).isEqualTo(createRestaurantRequest.getCityId());
        assertThat(responseEntity.getBody().dishes().size()).isEqualTo(0);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(responseEntity.getBody().id());
        assertThat(optionalRestaurant.isPresent()).isTrue();
        optionalRestaurant.ifPresent(r -> assertThat(r.getCity()).isNotNull());

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertThat(optionalCity.isPresent()).isTrue();
        optionalCity.ifPresent(c -> assertThat(c.getRestaurants().size()).isEqualTo(1));
    }

    @Test
    void testUpdateRestaurant() {
        City city = saveCity("Porto");
        Restaurant restaurant = saveRestaurant(city);

        City city2 = saveCity("Berlin");
        UpdateRestaurantRequest updateRestaurantRequest = new UpdateRestaurantRequest(city2.getId(), "Happy Burger");

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId());
        HttpEntity<UpdateRestaurantRequest> requestUpdate = new HttpEntity<>(updateRestaurantRequest);
        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.exchange(
                url, HttpMethod.PUT, requestUpdate, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(restaurant.getId());
        assertThat(responseEntity.getBody().name()).isEqualTo(updateRestaurantRequest.getName());
        assertThat(responseEntity.getBody().city().id()).isEqualTo(updateRestaurantRequest.getCityId());
        assertThat(responseEntity.getBody().dishes().size()).isEqualTo(0);

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
        City city = saveCity("Porto");
        Restaurant restaurant = saveRestaurant(city);

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_URL, restaurant.getId());
        ResponseEntity<RestaurantResponse> responseEntity = testRestTemplate.exchange(
                url, HttpMethod.DELETE, null, RestaurantResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(restaurant.getId());
        assertThat(responseEntity.getBody().name()).isEqualTo(restaurant.getName());
        assertThat(responseEntity.getBody().city().id()).isEqualTo(restaurant.getCity().getId());
        assertThat(responseEntity.getBody().city().name()).isEqualTo(restaurant.getCity().getName());
        assertThat(responseEntity.getBody().dishes().size()).isEqualTo(0);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertThat(optionalRestaurant.isPresent()).isFalse();

        Optional<City> optionalCity = cityRepository.findById(city.getId());
        assertThat(optionalCity.isPresent()).isTrue();
        optionalCity.ifPresent(c -> assertThat(c.getRestaurants().size()).isEqualTo(0));
    }

    private City saveCity(String name) {
        return cityRepository.save(new City(name));
    }

    private Restaurant saveRestaurant(City city) {
        Restaurant restaurant = new Restaurant("Happy Pizza", city);
        city.getRestaurants().add(restaurant);
        cityRepository.save(city);
        return restaurantRepository.save(restaurant);
    }

    private static final String API_RESTAURANTS_URL = "/api/restaurants";
    private static final String API_RESTAURANTS_RESTAURANT_ID_URL = "/api/restaurants/%s";
}