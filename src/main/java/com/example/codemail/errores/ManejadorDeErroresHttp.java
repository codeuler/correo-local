package com.example.codemail.errores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

/**
 * Esta interface tiene la finalidad de retornar un mensaje de error claro en caso de que cuando se realice una petición http
 * con un objeto embebido en el body del mensaje  y este no cumpla con las condiciones esperadas, se atrape el error y
 * se le notifique al usuario de una manera entendible para él
 */
public interface ManejadorDeErroresHttp {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    default ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        var errors = new HashMap<String,String>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
