package com.example.codemail.mensaje;

public class MensajeNoExisteExcepcion extends Exception {
    public MensajeNoExisteExcepcion(String mensaje) {
        super(mensaje);
    }
}
