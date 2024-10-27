package com.example.codemail.folder;

public class FolderNoExisteException extends Exception {

    public FolderNoExisteException(String mensajeUsuario) {
        super(mensajeUsuario);
    }

}
