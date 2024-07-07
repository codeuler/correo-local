package com.example.codemail.usuario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioDto create(UsuarioDto usuarioDto) {
        var usuario = usuarioMapper.toUsuario(usuarioDto);
        //Encriptar password
        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );
        System.out.println(usuario);
        usuarioRepository.save(usuario);
        return usuarioDto;
    }

}
