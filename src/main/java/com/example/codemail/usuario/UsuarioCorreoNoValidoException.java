package com.example.codemail.usuario;

public class UsuarioCorreoNoValidoException extends Exception {
    public UsuarioCorreoNoValidoException(String mensaje) {
        super(mensaje);
    }
}
