package br.feevale.bolao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController("error")
public class CustomizedExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomExceptionHandler.class)
    public final ResponseEntity<ApiError> handleUserNotFoundException(CustomExceptionHandler ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.OK, "teste 2", "meu erro");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

}
