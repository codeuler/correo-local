package com.example.codemail.mensaje;

import com.example.codemail.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    Optional<Mensaje> findByIdAndUsuario(Long id, Usuario usuario);
}
