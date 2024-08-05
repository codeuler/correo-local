package com.example.codemail.folder;

import com.example.codemail.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    Optional<Folder> findByNombreAndPropietario(String nombre, Usuario propietario);
}
