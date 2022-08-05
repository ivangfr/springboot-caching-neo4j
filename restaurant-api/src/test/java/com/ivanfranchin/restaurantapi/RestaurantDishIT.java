package com.ivanfranchin.restaurantapi;

import com.ivanfranchin.restaurantapi.repository.CityRepository;
import com.ivanfranchin.restaurantapi.repository.DishRepository;
import com.ivanfranchin.restaurantapi.repository.RestaurantRepository;
import com.ivanfranchin.restaurantapi.model.City;
import com.ivanfranchin.restaurantapi.model.Dish;
import com.ivanfranchin.restaurantapi.model.Restaurant;
import com.ivanfranchin.restaurantapi.rest.dto.CreateDishRequest;
import com.ivanfranchin.restaurantapi.rest.dto.DishResponse;
import com.ivanfranchin.restaurantapi.rest.dto.RestaurantMenu;
import com.ivanfranchin.restaurantapi.rest.dto.UpdateDishRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantDishIT extends AbstractTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private City city;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        city = saveDefaultCity();
        restaurant = saveDefaultRestaurant();
    }

    @Test
    void testGetRestaurantDish() {
        Dish dish = saveDefaultDish();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId());
        ResponseEntity<DishResponse> responseEntity = testRestTemplate.getForEntity(url, DishResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(dish.getId());
        assertThat(responseEntity.getBody().name()).isEqualTo(dish.getName());
        assertThat(responseEntity.getBody().price()).isEqualTo(dish.getPrice());
    }

    @Test
    void testGetRestaurantDishes() {
        Dish dish = saveDefaultDish();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId());
        ResponseEntity<RestaurantMenu> responseEntity = testRestTemplate.getForEntity(url, RestaurantMenu.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().dishes().size()).isEqualTo(1);
        assertThat(responseEntity.getBody().dishes().get(0).id()).isEqualTo(dish.getId());
        assertThat(responseEntity.getBody().dishes().get(0).name()).isEqualTo(dish.getName());
        assertThat(responseEntity.getBody().dishes().get(0).price()).isEqualTo(dish.getPrice());
    }

    @Test
    void testCreateRestaurantDish() {
        CreateDishRequest createDishRequest = new CreateDishRequest("Pizza Salami", BigDecimal.valueOf(7.5));

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId());
        ResponseEntity<DishResponse> responseEntity = testRestTemplate.postForEntity(
                url, createDishRequest, DishResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isNotNull();
        assertThat(responseEntity.getBody().name()).isEqualTo(createDishRequest.getName());
        assertThat(responseEntity.getBody().price()).isEqualTo(createDishRequest.getPrice());

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertThat(optionalRestaurant.isPresent()).isTrue();
        optionalRestaurant.ifPresent(r -> {
            assertThat(r.getDishes().size()).isEqualTo(1);
            assertThat(r.getCity()).isNotNull();
        });
    }

    @Test
    void testUpdateRestaurantDish() {
        Dish dish = saveDefaultDish();

        UpdateDishRequest updateDishRequest = new UpdateDishRequest("Pizza Tuna", BigDecimal.valueOf(8.5));

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId());
        HttpEntity<UpdateDishRequest> requestUpdate = new HttpEntity<>(updateDishRequest);
        ResponseEntity<DishResponse> responseEntity = testRestTemplate.exchange(
                url, HttpMethod.PUT, requestUpdate, DishResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(dish.getId());
        assertThat(responseEntity.getBody().name()).isEqualTo(updateDishRequest.getName());
        assertThat(responseEntity.getBody().price()).isEqualTo(updateDishRequest.getPrice());

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertThat(optionalRestaurant.isPresent()).isTrue();
        optionalRestaurant.ifPresent(r -> {
            assertThat(r.getDishes().size()).isEqualTo(1);
            assertThat(r.getCity()).isNotNull();
            assertThat(r.getCity().getRestaurants().size()).isEqualTo(1);
        });
    }

    @Test
    void testDeleteRestaurantDish() {
        Dish dish = saveDefaultDish();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId());
        ResponseEntity<DishResponse> responseEntity = testRestTemplate.exchange(
                url, HttpMethod.DELETE, null, DishResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(dish.getId());
        assertThat(responseEntity.getBody().name()).isEqualTo(dish.getName());
        assertThat(responseEntity.getBody().price()).isEqualTo(dish.getPrice());

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertThat(optionalRestaurant.isPresent()).isTrue();
        optionalRestaurant.ifPresent(r -> {
            assertThat(r.getDishes().size()).isEqualTo(0);
            assertThat(r.getCity()).isNotNull();
            assertThat(r.getCity().getRestaurants().size()).isEqualTo(1);
        });
    }

    private Dish saveDefaultDish() {
        Dish defaultDish = new Dish();
        defaultDish.setName("Pizza Salami");
        defaultDish.setPrice(BigDecimal.valueOf(7.5));
        defaultDish = dishRepository.save(defaultDish);

        restaurant.getDishes().add(defaultDish);
        restaurantRepository.save(restaurant);
        return defaultDish;
    }

    private Restaurant saveDefaultRestaurant() {
        Restaurant defaultRestaurant = new Restaurant();
        defaultRestaurant.setName("Happy Pizza");
        defaultRestaurant.setCity(city);

        city.getRestaurants().add(defaultRestaurant);
        cityRepository.save(city);

        return restaurantRepository.save(defaultRestaurant);
    }

    private City saveDefaultCity() {
        City defaultCity = new City();
        defaultCity.setName("Porto");
        return cityRepository.save(defaultCity);
    }

    private static final String API_RESTAURANTS_RESTAURANT_ID_DISHES_URL = "/api/restaurants/%s/dishes";
    private static final String API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL = "/api/restaurants/%s/dishes/%s";
}