package com.uade.e_commerce.common;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " does not exist");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
