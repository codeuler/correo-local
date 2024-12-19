package com.example.codemail.usuario;

import com.example.codemail.carpeta.Carpeta;
import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensajepropietario.MensajePropietario;
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
    private RolUsuario rolUsuario;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    Set<Carpeta> carpetas = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    Set<Mensaje> mensajesEnviados = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    Set<MensajePropietario> mensajeDestinatario = new HashSet<>();

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String password, RolUsuario rolUsuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rolUsuario = rolUsuario;
    }

    public Set<Carpeta> getFolders() {
        return carpetas;
    }

    public void setFolders(Set<Carpeta> carpetas) {
        this.carpetas = carpetas;
    }

    public Set<Mensaje> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajeEnviados(Set<Mensaje> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

    public Set<MensajePropietario> getMensajesDestinatario() {
        return mensajeDestinatario;
    }

    public void setMensajeDestinatario(Set<MensajePropietario> mensajesDestinatario) {
        this.mensajeDestinatario = mensajesDestinatario;
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

    public RolUsuario getRol() {
        return rolUsuario;
    }

    public void setRol(RolUsuario rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    //Metodos implementados de la interfaz
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Retorna una lista de un objeto que representa el rol del usuario
        return List.of(new SimpleGrantedAuthority(rolUsuario.name()));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
