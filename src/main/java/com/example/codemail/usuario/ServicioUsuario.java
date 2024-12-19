package com.example.codemail.usuario;

import org.springframework.stereotype.Service;

@Service
public class ServicioUsuario {
    UsuarioMapeador usuarioMapeador;

    public ServicioUsuario(UsuarioMapeador usuarioMapeador) {
        this.usuarioMapeador = usuarioMapeador;
    }

    public InformacionUsuario getInformacion(Usuario usuario) {
        return usuarioMapeador.toUsuarioInformacion(usuario);
    }
}
