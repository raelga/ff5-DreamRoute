package com.Travellers.DreamRoute.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " not found with id " + id);
    }

    public EntityNotFoundException(String entityName, String username) {
        super(entityName + " not found with username " + username);
    }
}