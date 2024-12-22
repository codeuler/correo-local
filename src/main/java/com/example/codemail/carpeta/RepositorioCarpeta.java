package com.example.codemail.carpeta;

import com.example.codemail.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RepositorioCarpeta extends JpaRepository<Carpeta, Integer> {
    Optional<Carpeta> findByNombreAndPropietario(String nombre, Usuario propietario);
    Optional<Carpeta> findByIdAndPropietario(Integer id, Usuario propietario);
    Set<Carpeta> findAllByPropietario(Usuario propietario);
    List<Carpeta> findAllByPropietarioOrderByNombreAsc(Usuario propietario);
}
