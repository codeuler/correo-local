package com.example.codemail.usuario;

import com.example.codemail.folder.Folder;
import com.example.codemail.mensaje.Mensaje;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(
            nullable = false,
            length = 15
    )
    private String nombre;

    @Column(
            nullable = false,
            length = 15
    )
    private String apellido;

    @Column(
            unique = true,
            length = 30
    )
    private String email;
    @Column(
            nullable = false
    )
    private String password;
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToMany(mappedBy = "propietario",cascade = CascadeType.ALL)
    Set<Folder> folders = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    Set<Mensaje> mensajesEnviados = new HashSet<>();

    @ManyToMany
    @JoinTable(name="Mensajes_Destinatarios",
                joinColumns = @JoinColumn(name="usuario_id"),
                inverseJoinColumns = @JoinColumn(name="mensaje_id"))
    Set<Mensaje> mensajesDestinatario = new HashSet<>();

    public Usuario () {

    }

    public Usuario(String nombre, String apellido, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }

    public Set<Mensaje> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(Set<Mensaje> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

    public Set<Mensaje> getMensajesDestinatario() {
        return mensajesDestinatario;
    }

    public void setMensajesDestinatario(Set<Mensaje> mensajesDestinatario) {
        this.mensajesDestinatario = mensajesDestinatario;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    //Metodos implementados de la interfaz
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Retorna una lista de un objeto que representa el rol del usuario
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
