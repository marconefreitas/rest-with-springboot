package br.com.marconefreitas.exception.handler;

import br.com.marconefreitas.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestController
@ControllerAdvice
public class CustomEntityResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionReponse> handleAllExceptions(Exception ex, WebRequest req){
        ExceptionReponse res = new ExceptionReponse(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionReponse> handleNotFoundExceptions(Exception ex, WebRequest req){
        ExceptionReponse res = new ExceptionReponse(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequiredObjectNullException.class)
    public final ResponseEntity<ExceptionReponse> handleRequiredObjectExceptions(Exception ex, WebRequest req){
        ExceptionReponse res = new ExceptionReponse(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ExceptionReponse> handleBadRequestExceptions(Exception ex, WebRequest req){
        ExceptionReponse res = new ExceptionReponse(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public final ResponseEntity<ExceptionReponse> handleFileNotFoundExceptions(Exception ex, WebRequest req){
        ExceptionReponse res = new ExceptionReponse(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileStorageException.class)
    public final ResponseEntity<ExceptionReponse> handleFileStorageExceptions(Exception ex, WebRequest req){
        ExceptionReponse res = new ExceptionReponse(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidJWTAuthenticationException.class)
    public final ResponseEntity<ExceptionReponse> handleInvalidJWTAuthenticationExceptions(Exception ex, WebRequest req){
        ExceptionReponse res = new ExceptionReponse(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
    }

}
