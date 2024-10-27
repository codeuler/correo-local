package com.example.codemail.mensajepropietario;

public class MensajePropietarioNoExisteException extends Exception {
    public MensajePropietarioNoExisteException(String mensaje) {
        super(mensaje);
    }
}
