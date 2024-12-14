package com.example.carrie.errors.custom;

public class BadRequest extends RuntimeException {
    public BadRequest(String message){
        super(message);
    }
}


