package com.example.carrie.errors.custom;

public class NotFound extends RuntimeException {
    public NotFound(String message) {
        super(message);
    }
}
