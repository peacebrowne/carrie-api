package com.example.carrie.success;

import java.util.List;

public class SuccessDetails {
    private String message;
    private List<?> data;
    private int status;

    public SuccessDetails(String message, int status, List<?> data) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public SuccessDetails(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setDetails(int status) {
        this.status = status;
    }
}
