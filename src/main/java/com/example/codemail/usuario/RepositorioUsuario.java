package com.example.codemail.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioUsuario extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
