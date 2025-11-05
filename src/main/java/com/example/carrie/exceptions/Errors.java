package com.example.carrie.exceptions;

import com.example.carrie.exceptions.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class Errors extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BadRequest.class)
  public ResponseEntity<?> BAD_REQUEST(BadRequest ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false),
        HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotFound.class)
  public ResponseEntity<?> NOT_FOUND(NotFound ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false),
        HttpStatus.NOT_FOUND.value());
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InternalServerError.class)
  public ResponseEntity<?> INTERNAL_SERVER_ERROR(InternalServerError ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false),
        HttpStatus.INTERNAL_SERVER_ERROR.value());
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Conflict.class)
  public ResponseEntity<?> CONFLICT(Conflict ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false),
        HttpStatus.CONFLICT.value());
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  @ExceptionHandler({ AuthenticationException.class })
  public ResponseEntity<?> UNAUTHORIZED(AuthenticationException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
            request.getDescription(false),
            HttpStatus.UNAUTHORIZED.value());
    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

}
