package com.mycompany.springbootneo4jcaffeine.exception;

import java.util.UUID;

public class CityNotFoundException extends Exception {

    public CityNotFoundException(UUID cityId) {
        super(String.format("City id '%s' not found", cityId));
    }
}
