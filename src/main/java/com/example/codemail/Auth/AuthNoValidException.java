package com.example.codemail.Auth;

public class AuthNoValidException extends Exception{
    public AuthNoValidException(String mensaje){
        super(mensaje);
    }
}
