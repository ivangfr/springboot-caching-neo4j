package com.mycompany.springbootneo4jcaffeine.exception;

public class MapperException extends RuntimeException {

    public MapperException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
