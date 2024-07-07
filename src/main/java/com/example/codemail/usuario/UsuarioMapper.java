package com.example.codemail.usuario;

import org.springframework.stereotype.Service;

@Service
public class UsuarioMapper {

    public Usuario toUsuario(UsuarioDto usuarioDto) {
        return new Usuario(
                usuarioDto.nombre(),
                usuarioDto.apellido(),
                usuarioDto.correo(),
                usuarioDto.password(),
                Rol.USUARIO
        );
    }

    public UsuarioDto usuarioDto(Usuario usuario) {
        return new UsuarioDto(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getPassword()
        );
    }

}
