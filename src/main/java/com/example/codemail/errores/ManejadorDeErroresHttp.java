package com.example.codemail.errores;

import com.example.codemail.autenticacion.AutenticacionNoValidaExcepcion;
import com.example.codemail.autenticacion.ErrorRegistroExcepcion;
import com.example.codemail.folder.FolderImposibleEliminarException;
import com.example.codemail.folder.FolderNoExisteException;
import com.example.codemail.folder.FolderYaExisteException;
import com.example.codemail.mensaje.MensajeErrorCambioFolderException;
import com.example.codemail.mensaje.MensajeNoExisteException;
import com.example.codemail.mensaje.MensajePerteneceCarpetaOrigenException;
import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteException;
import com.example.codemail.usuario.UsuarioCorreoNoValidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class ManejadorDeErroresHttp {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(value = {
            FolderImposibleEliminarException.class,
            FolderYaExisteException.class,
            MensajeErrorCambioFolderException.class,
            MensajePerteneceCarpetaOrigenException.class,
            UsuarioCorreoNoValidoException.class,
            ErrorRegistroExcepcion.class
    })
    public ResponseEntity<String> handleConflictException(
            Exception e
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(value = {
            FolderNoExisteException.class,
            MensajeNoExisteException.class,
            MensajePropietarioNoExisteException.class
    })
    public ResponseEntity<String> handleNotFoundException(
            Exception e
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(value = {
            AutenticacionNoValidaExcepcion.class
    })
    public ResponseEntity<String> handleAuthNoValidException(
            Exception e
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
