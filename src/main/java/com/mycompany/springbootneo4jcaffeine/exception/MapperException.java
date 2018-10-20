package com.mycompany.springbootneo4jcaffeine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MapperException extends RuntimeException {

    public MapperException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
