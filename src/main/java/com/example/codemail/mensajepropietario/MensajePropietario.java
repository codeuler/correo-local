package com.example.codemail.mensajepropietario;

import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.usuario.Usuario;
import jakarta.persistence.*;

@Entity
public class MensajePropietario {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "mensaje_id")
    Mensaje mensaje;

    Boolean revisado;

    public MensajePropietario() {}

    public MensajePropietario(Usuario usuario, Mensaje mensaje, Boolean revisado) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.revisado = revisado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getRevisado() {
        return revisado;
    }

    public void setRevisado(Boolean revisado) {
        this.revisado = revisado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}