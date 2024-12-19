package com.example.codemail.usuario;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControladorUsuario {
    ServicioUsuario servicioUsuario;

    public ControladorUsuario(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    @GetMapping("/usuarios/informacion")
    public InformacionUsuario getInformacion(@AuthenticationPrincipal Usuario usuario) {
        return servicioUsuario.getInformacion(usuario);
    }
}
