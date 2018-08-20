package com.mycompany.springbootneo4jcaffeine.exception;

public class CityNotFoundException extends Exception {

    public CityNotFoundException(String cityId) {
        super(String.format("City id '%s' not found", cityId));
    }
}
