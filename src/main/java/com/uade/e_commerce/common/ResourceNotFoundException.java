package com.uade.e_commerce.common;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " con id " + id + " no existe");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
