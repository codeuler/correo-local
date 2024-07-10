package com.example.codemail.usuario;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsuarioControlador {

    private final UsuarioService usuarioService;

    public UsuarioControlador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/usuarios")
    public UsuarioDto crearUsuario (
            @Valid @RequestBody UsuarioDto usuarioDto
    ) {

        return usuarioService.create(usuarioDto);
    }

}
