package com.uade.e_commerce.common;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String resourceName, String field, String value) {
        super(resourceName + " ya existe con " + field + ": " + value);
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}

