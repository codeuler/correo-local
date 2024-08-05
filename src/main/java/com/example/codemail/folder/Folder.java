package com.example.codemail.folder;

import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.usuario.Usuario;
import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
public class Folder {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private ArrayList<Mensaje> mensajes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    public Folder(String nombre) {
        this.nombre = nombre;
    }

    public Folder() {

    }

    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<Mensaje> mensajes) {
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
