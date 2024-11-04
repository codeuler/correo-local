package com.example.codemail.usuario;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioInformacion getInformacion(Usuario usuario) {
        return usuarioMapper.toUsuarioInformacion(usuario);
    }
}
