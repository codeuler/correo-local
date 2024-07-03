package com.example.codemail.usuario;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioDto create(UsuarioDto usuarioDto) {
        var usuario = usuarioMapper.toUsuario(usuarioDto);
        usuarioRepository.save(usuario);
        return usuarioDto;
    }

}
