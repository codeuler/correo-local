package com.example.codemail.mensaje;

import com.example.codemail.carpeta.Carpeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RepositorioMensaje extends JpaRepository<Mensaje, Long> {
    Optional<Mensaje> findByIdAndFolder(Long id, Set<Carpeta> carpeta);
}
