package com.example.carrie.success;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Success {
    static public ResponseEntity<?> CREATED(String message, List<?> data) {

        HttpStatus status = HttpStatus.CREATED;

        List<?> safeData = data == null ? List.of() : data;

        SuccessDetails successDetails = safeData.isEmpty() ? new SuccessDetails(message, status.value())
                : new SuccessDetails(message, status.value(), data);

        return new ResponseEntity<>(successDetails, status);

    }

    static public ResponseEntity<?> OK(String message, List<?> data) {

        HttpStatus status = HttpStatus.OK;

        List<?> safeData = data == null ? List.of() : data;

        SuccessDetails successDetails = safeData.isEmpty() ? new SuccessDetails(message, status.value())
                : new SuccessDetails(message, status.value(), data);

        return new ResponseEntity<>(successDetails, status);

    }

}
