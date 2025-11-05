package com.example.carrie.exceptions.custom;

public class NotFound extends RuntimeException {
    public NotFound(String message) {
        super(message);
    }
}
