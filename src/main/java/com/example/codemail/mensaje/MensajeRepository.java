package com.example.codemail.mensaje;

import com.example.codemail.folder.Folder;
import com.example.codemail.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    Optional<Mensaje> findByIdAndUsuario(Long id, Usuario usuario);

    Set<Mensaje> findAllByUsuarioAndFolder(Usuario usuario, Set<Folder> folder);
}
