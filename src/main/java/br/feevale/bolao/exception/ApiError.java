package br.feevale.bolao.exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class ApiError {

    private HttpStatus status;

    private String message;

    private List<String> errors;

    public ApiError(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, String error) {
        this.status = status;
        this.message = message;
        errors = new ArrayList<>();
        errors.add(error);
    }

}
