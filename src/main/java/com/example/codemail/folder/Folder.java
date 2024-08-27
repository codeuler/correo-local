package com.example.codemail.folder;

import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.usuario.Usuario;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Folder {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @ManyToMany()
    /*
     * Esta es la entidad que va a controlar la union de las tablas, cada uno de los nombres indica como se van a llamar las columnas de la tabla intermedia
     * JuoinColumns es para el id de esta entidad y el inverse es para el id de la otra entidad
     */
    @JoinTable(
            name = "Mensajes_Folders",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "mensaje_id")
    )
    private Set<Mensaje> mensajes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    public Folder(String nombre, Usuario propietario) {
        this.nombre = nombre;
        this.propietario = propietario;
    }

    public Folder() {

    }

    public Set<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(Set<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String name) {
        this.nombre = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
