package com.mycompany.springbootneo4jcaffeine;

import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.repository.CityRepository;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CityDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateCityDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CityIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void testGetCity() {
        City city = saveDefaultCity();

        String url = String.format("/api/cities/%s", city.getId());
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

        ResponseEntity<CityDto> responseEntity = testRestTemplate.postForEntity("/api/cities", createCityDto, CityDto.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getId());
        assertEquals(createCityDto.getName(), responseEntity.getBody().getName());
        assertEquals(0, responseEntity.getBody().getRestaurants().size());
    }

    @Test
    void testDeleteCity() {
        City city = saveDefaultCity();

        String url = String.format("/api/cities/%s", city.getId());
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
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
}