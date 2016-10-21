package com.sap.bulletinboard.ads.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * A simple exception mapper for exceptions that also provides the error messages as part of the response. Gathers
 * all @ExceptionHandler methods in a single class so that exceptions from all controllers are handled consistently in
 * one place.
 */
@RestControllerAdvice
public class CustomExceptionMapper extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
                    + violation.getMessage() + " [current value = " + violation.getInvalidValue() + "]");
        }
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage(),
                errors.toArray(new String[errors.size()]));

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    /**
     * Here we have to override implementation of ResponseEntityExceptionHandler.
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        return convertToResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        return convertToResponseEntity(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        return convertToResponseEntity(exception, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler
    public ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException exception) {
        return convertToResponseEntity(exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAll(Exception exception) {
        return convertToResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> convertToResponseEntity(Exception exception, HttpStatus status) {
        ApiError apiError = new ApiError(status, exception.getLocalizedMessage(),
                exception.getClass().getSimpleName() + ": error occurred");

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    /**
     * Simple structure for sending errors as JSON
     */
    public class ApiError {
        private HttpStatus status;
        private String message;
        private List<String> errors;

        public ApiError(HttpStatus status, String message, String... errors) {
            this.status = status;
            this.message = message;
            this.errors = Arrays.asList(errors);
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}
