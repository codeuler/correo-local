package com.example.codemail.mensaje;

import com.example.codemail.folder.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RepositorioMensaje extends JpaRepository<Mensaje, Long> {
    Optional<Mensaje> findByIdAndFolder(Long id, Set<Folder> folder);
}
