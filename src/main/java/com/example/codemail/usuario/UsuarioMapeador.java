package com.example.codemail.usuario;

import com.example.codemail.autenticacion.PeticionRegistro;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioMapeador {
    private final PasswordEncoder passwordEncoder;

    public UsuarioMapeador(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario toUsuario(PeticionRegistro peticionRegistro) {
        return new Usuario(
                peticionRegistro.nombre(),
                peticionRegistro.apellido(),
                peticionRegistro.correo(),
                //Encriptar password
                passwordEncoder.encode(peticionRegistro.password()),
                RolUsuario.USUARIO
        );
    }

    public InformacionUsuario toUsuarioInformacion(Usuario usuario) {
        return new InformacionUsuario(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail()
        );
    }

}
