package com.example.codemail.mensaje;

public class MensajeNoExisteException extends Exception {
    public MensajeNoExisteException(String mensaje) {
        super(mensaje);
    }
}
