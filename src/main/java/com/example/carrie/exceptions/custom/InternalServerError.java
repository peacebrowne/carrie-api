package com.example.carrie.exceptions.custom;

public class InternalServerError extends RuntimeException{
    public InternalServerError(String message){
        super(message);
    }
}
