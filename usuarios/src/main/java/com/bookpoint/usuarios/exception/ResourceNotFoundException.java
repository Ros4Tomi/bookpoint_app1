package com.bookpoint.usuarios.exception;

/**
 * excepcion lanzada cuando un usuario o recurso no existe
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}