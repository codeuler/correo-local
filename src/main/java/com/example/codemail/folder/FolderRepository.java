package com.example.codemail.folder;

import com.example.codemail.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    Optional<Folder> findByNombreAndPropietario(String nombre, Usuario propietario);
    Optional<Folder> findByIdAndPropietario(Integer id, Usuario propietario);
    Set<Folder> findAllByPropietario(Usuario propietario);
}
