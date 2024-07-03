package com.example.codemail.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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
            length = 30,
            nullable = false
    )

    private String password;

    public Usuario () {

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
}
