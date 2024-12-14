package com.example.carrie.errors.custom;

public class InternalServerError extends RuntimeException{
    public InternalServerError(String message){
        super(message);
    }
}
