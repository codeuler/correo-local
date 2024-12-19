package com.example.codemail.mensaje;

import com.example.codemail.carpeta.Carpeta;
import com.example.codemail.mensajepropietario.MensajePropietario;
import com.example.codemail.usuario.Usuario;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Mensaje {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false)
    private String cuerpo;
    @Column(
            name = "fecha_envio",
            nullable = false
    )
    private Date fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "remitente_id")
    private Usuario usuario;

    @ManyToMany(mappedBy = "mensajes")
    private Set<Carpeta> carpeta;

    @OneToMany(mappedBy = "mensaje")
    private Set<MensajePropietario> mensajeDestinatario = new HashSet<>();

    public Mensaje() {
    }

    public Mensaje(String asunto, String cuerpo, Date fechaEnvio, Set<Carpeta> carpeta, Usuario usuario) {
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.fechaEnvio = fechaEnvio;
        this.carpeta = carpeta;
        this.usuario = usuario;
    }

    public Set<MensajePropietario> getMensajeDestinatario() {
        return mensajeDestinatario;
    }

    public void setMensajeDestinatario(Set<MensajePropietario> destinatarios) {
        this.mensajeDestinatario = destinatarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Set<Carpeta> getFolder() {
        return carpeta;
    }

    public void setFolder(Set<Carpeta> carpeta) {
        this.carpeta = carpeta;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
