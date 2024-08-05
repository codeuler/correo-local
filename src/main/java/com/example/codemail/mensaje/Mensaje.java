package com.example.codemail.mensaje;

import com.example.codemail.folder.Folder;
import com.example.codemail.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="remitente_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="folder_id")
    private Folder folder;

    @ManyToMany(mappedBy = "mensajesDestinatario")
    private Set<Usuario> destinatarios = new HashSet<>();

    @NotNull
    private boolean revisado;

    public Mensaje() {}

    public Mensaje(String asunto, String cuerpo, Date fechaEnvio, Folder folder, Usuario usuario, Set<Usuario> destinatarios, boolean revisado) {
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.fechaEnvio = fechaEnvio;
        this.folder = folder;
        this.usuario = usuario;
        this.destinatarios = destinatarios;
        this.revisado = revisado;
    }

    public boolean isRevisado() {
        return revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }

    public Set<Usuario> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(Set<Usuario> destinatarios) {
        this.destinatarios = destinatarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
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
