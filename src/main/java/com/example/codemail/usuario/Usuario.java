package com.example.codemail.usuario;

import jakarta.persistence.*;

@Entity
public class Usuario {
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
    public Usuario () {

    }

    public Usuario(String nombre, String apellido, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
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

    public String getPassword() {
        return password;
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
}
