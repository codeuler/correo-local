package com.example.codemail.usuario;

public class CorreoNoValidoExcepcion extends Exception {
    public CorreoNoValidoExcepcion(String mensaje) {
        super(mensaje);
    }
}
