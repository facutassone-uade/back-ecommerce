package com.uade.e_commerce.common;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String resourceName, String field, String value) {
        super(resourceName + " already exists with " + field + ": " + value);
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}

