package br.com.marconefreitas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{

    public BadRequestException(String msg) {
        super("Unsupported file extension");
    }

    public BadRequestException() {
        super("It is not allowed persist a null object");
    }
}
