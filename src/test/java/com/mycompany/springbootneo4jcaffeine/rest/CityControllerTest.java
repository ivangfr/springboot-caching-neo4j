package com.mycompany.springbootneo4jcaffeine.rest;

import com.mycompany.springbootneo4jcaffeine.exception.CityNotFoundException;
import com.mycompany.springbootneo4jcaffeine.model.City;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CityDto;
import com.mycompany.springbootneo4jcaffeine.rest.dto.CreateCityDto;
import com.mycompany.springbootneo4jcaffeine.service.CityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CityControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private CityService cityService;

    @Test
    void testGetCityCaching() throws Exception {
        City city = getDefaultCity();
        given(cityService.validateAndGetCityById(city.getId())).willReturn(city);

        String url = String.format("/api/cities/%s", city.getId());
        ResponseEntity<CityDto> responseEntity = testRestTemplate.getForEntity(url, CityDto.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        responseEntity = testRestTemplate.getForEntity(url, CityDto.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(cityService, times(1)).validateAndGetCityById(city.getId());
    }

    @Test
    void testCreateCityCaching() throws CityNotFoundException {
        City city = getDefaultCity();
        CreateCityDto createCityDto = getDefaultCreateCityDto();

        given(cityService.validateAndGetCityById(city.getId())).willReturn(city);
        given(cityService.saveCity(any(City.class))).willReturn(city);

        ResponseEntity<CityDto> responseEntity = testRestTemplate.postForEntity("/api/cities", createCityDto, CityDto.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        String url = String.format("/api/cities/%s", city.getId());
        responseEntity = testRestTemplate.getForEntity(url, CityDto.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(cityService, times(0)).validateAndGetCityById(city.getId());
    }

    @Test
    void testDeleteCityCaching() throws CityNotFoundException {
        City city = getDefaultCity();

        given(cityService.validateAndGetCityById(city.getId())).willReturn(city);

        String url = String.format("/api/cities/%s", city.getId());
        ResponseEntity<CityDto> responseEntityGet = testRestTemplate.getForEntity(url, CityDto.class);
        assertEquals(HttpStatus.OK, responseEntityGet.getStatusCode());

        ResponseEntity<Void> responseEntityDelete = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, responseEntityDelete.getStatusCode());

        responseEntityGet = testRestTemplate.getForEntity(url, CityDto.class);
        assertEquals(HttpStatus.OK, responseEntityGet.getStatusCode());

        verify(cityService, times(3)).validateAndGetCityById(city.getId());
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