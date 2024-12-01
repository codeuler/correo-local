package com.example.codemail.auth;

public class AuthNoValidException extends Exception{
    public AuthNoValidException(String mensaje){
        super(mensaje);
    }
}
