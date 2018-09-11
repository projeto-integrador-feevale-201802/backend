package br.feevale.bolao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class CustomExceptionHandler extends RuntimeException {

    private Integer code;
    private String message;
    private Exception ex;

    public CustomExceptionHandler(Integer code, String message, Exception ex) {
        super(message);
        this.code = code;
        this.message = message;
        this.ex = ex;
    }

}
