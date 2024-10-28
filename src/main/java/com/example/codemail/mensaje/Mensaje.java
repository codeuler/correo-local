package com.example.codemail.mensaje;

import com.example.codemail.folder.Folder;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "remitente_id")
    private Usuario usuario;

    @ManyToMany(mappedBy = "mensajes", fetch = FetchType.EAGER)
    private Set<Folder> folder;

    @OneToMany(mappedBy = "mensaje", fetch = FetchType.EAGER)
    private Set<MensajePropietario> mensajeDestinatario = new HashSet<>();

    public Mensaje() {
    }

    public Mensaje(String asunto, String cuerpo, Date fechaEnvio, Set<Folder> folder, Usuario usuario) {
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.fechaEnvio = fechaEnvio;
        this.folder = folder;
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

    public Set<Folder> getFolder() {
        return folder;
    }

    public void setFolder(Set<Folder> folder) {
        this.folder = folder;
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
