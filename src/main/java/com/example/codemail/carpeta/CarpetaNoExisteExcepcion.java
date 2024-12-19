package com.example.codemail.carpeta;

public class CarpetaNoExisteExcepcion extends Exception {

    public CarpetaNoExisteExcepcion(String mensajeUsuario) {
        super(mensajeUsuario);
    }

}
