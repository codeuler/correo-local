package com.example.codemail.mensajepropietario;

import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MensajePropietarioRepository extends JpaRepository<MensajePropietario,Long> {
    Optional<MensajePropietario> findByUsuarioAndMensaje(Usuario usuario, Mensaje mensaje);
}
