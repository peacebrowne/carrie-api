package com.example.carrie.success;

import lombok.Data;

@Data
public class SuccessDetails<T> {
    private String message;
    private T data;
    private int status;

    public SuccessDetails(String message, int status, T data) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public SuccessDetails(String message, int status) {
        this.message = message;
        this.status = status;
    }

}
