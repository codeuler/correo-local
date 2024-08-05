package com.example.codemail.usuario;

import org.springframework.web.bind.annotation.*;

@RestController
public class UsuarioControlador {

    private final UsuarioService usuarioService;

    public UsuarioControlador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

}
