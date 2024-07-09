package com.example.codemail.usuario;

import com.example.codemail.Auth.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioMapper {
    private final PasswordEncoder passwordEncoder;

    public UsuarioMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario toUsuario(UsuarioDto usuarioDto) {
        return new Usuario(
                usuarioDto.nombre(),
                usuarioDto.apellido(),
                usuarioDto.correo(),
                usuarioDto.password(),
                Rol.USUARIO
        );
    }

    public Usuario toUsuario(RegisterRequest registerRequest) {
        return new Usuario(
                registerRequest.nombre(),
                registerRequest.apellido(),
                registerRequest.correo(),
                //Encriptar password
                passwordEncoder.encode(registerRequest.password()),
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
