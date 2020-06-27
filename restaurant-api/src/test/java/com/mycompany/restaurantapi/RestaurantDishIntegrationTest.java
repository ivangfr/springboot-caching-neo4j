package com.mycompany.restaurantapi;

import com.mycompany.restaurantapi.model.City;
import com.mycompany.restaurantapi.model.Dish;
import com.mycompany.restaurantapi.model.Restaurant;
import com.mycompany.restaurantapi.repository.CityRepository;
import com.mycompany.restaurantapi.repository.DishRepository;
import com.mycompany.restaurantapi.repository.RestaurantRepository;
import com.mycompany.restaurantapi.rest.dto.CreateDishDto;
import com.mycompany.restaurantapi.rest.dto.DishDto;
import com.mycompany.restaurantapi.rest.dto.RestaurantMenu;
import com.mycompany.restaurantapi.rest.dto.UpdateDishDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantDishIntegrationTest {

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
        ResponseEntity<DishDto> responseEntity = testRestTemplate.getForEntity(url, DishDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(dish.getId(), responseEntity.getBody().getId());
        assertEquals(dish.getName(), responseEntity.getBody().getName());
        assertEquals(dish.getPrice(), responseEntity.getBody().getPrice());
    }

    @Test
    void testGetRestaurantDishes() {
        Dish dish = saveDefaultDish();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId());
        ResponseEntity<RestaurantMenu> responseEntity = testRestTemplate.getForEntity(url, RestaurantMenu.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getDishes().size());
        assertEquals(dish.getId(), responseEntity.getBody().getDishes().get(0).getId());
        assertEquals(dish.getName(), responseEntity.getBody().getDishes().get(0).getName());
        assertEquals(dish.getPrice(), responseEntity.getBody().getDishes().get(0).getPrice());
    }

    @Test
    void testCreateRestaurantDish() {
        CreateDishDto createDishDto = getDefaultCreateDishDto();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_URL, restaurant.getId());
        ResponseEntity<DishDto> responseEntity = testRestTemplate.postForEntity(url, createDishDto, DishDto.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getId());
        assertEquals(createDishDto.getName(), responseEntity.getBody().getName());
        assertEquals(createDishDto.getPrice(), responseEntity.getBody().getPrice());

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertTrue(optionalRestaurant.isPresent());
        optionalRestaurant.ifPresent(r -> assertEquals(1, r.getDishes().size()));
    }

    @Test
    void testUpdateRestaurantDish() {
        Dish dish = saveDefaultDish();

        UpdateDishDto updateDishDto = getDefaultUpdateDishDto();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId());
        HttpEntity<UpdateDishDto> requestUpdate = new HttpEntity<>(updateDishDto);
        ResponseEntity<DishDto> responseEntity = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, DishDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(dish.getId(), responseEntity.getBody().getId());
        assertEquals(updateDishDto.getName(), responseEntity.getBody().getName());
        assertEquals(updateDishDto.getPrice(), responseEntity.getBody().getPrice());
    }

    @Test
    void testDeleteRestaurantDish() {
        Dish dish = saveDefaultDish();

        String url = String.format(API_RESTAURANTS_RESTAURANT_ID_DISHES_DISH_ID_URL, restaurant.getId(), dish.getId());
        ResponseEntity<DishDto> responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, null, DishDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(dish.getId(), responseEntity.getBody().getId());
        assertEquals(dish.getName(), responseEntity.getBody().getName());
        assertEquals(dish.getPrice(), responseEntity.getBody().getPrice());

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getId());
        assertTrue(optionalRestaurant.isPresent());
        optionalRestaurant.ifPresent(r -> assertEquals(0, r.getDishes().size()));
    }

    private CreateDishDto getDefaultCreateDishDto() {
        CreateDishDto createDishDto = new CreateDishDto();
        createDishDto.setName("Pizza Salami");
        createDishDto.setPrice(BigDecimal.valueOf(7.5));
        return createDishDto;
    }

    private UpdateDishDto getDefaultUpdateDishDto() {
        UpdateDishDto updateDishDto = new UpdateDishDto();
        updateDishDto.setName("Pizza Tuna");
        updateDishDto.setPrice(BigDecimal.valueOf(8.5));
        return updateDishDto;
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