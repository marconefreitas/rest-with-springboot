package br.com.marconefreitas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectNullException extends RuntimeException{

    public RequiredObjectNullException(String msg) {
        super(msg);
    }

    public RequiredObjectNullException() {
        super("It is not allowed persist a null object");
    }
}
