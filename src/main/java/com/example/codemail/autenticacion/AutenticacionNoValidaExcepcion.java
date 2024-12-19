package com.example.codemail.autenticacion;

public class AutenticacionNoValidaExcepcion extends Exception{
    public AutenticacionNoValidaExcepcion(String mensaje){
        super(mensaje);
    }
}
