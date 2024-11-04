package com.example.codemail.usuario;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioControlador {
    UsuarioService usuarioService;

    public UsuarioControlador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios/informacion")
    public UsuarioInformacion getInformacion(@AuthenticationPrincipal Usuario usuario) {
        return usuarioService.getInformacion(usuario);
    }
}
