package com.example.codemail.usuario;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioControlador {
    @PostMapping("/usuarios")
    public Usuario crearUsuario (
            @RequestBody Usuario usuario
    ) {
        return usuario;
    }
}
