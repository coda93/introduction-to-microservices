package com.example.resource_service.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Integer id) {
        super("Resource with ID=" + id + " not found");
    }
}
