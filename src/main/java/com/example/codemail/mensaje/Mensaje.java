package com.example.codemail.mensaje;

import com.example.codemail.folder.Folder;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="remitente_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="folder_id")
    private Folder folder;

    @ManyToMany(mappedBy = "mensajesDestinatario")
    private Set<Usuario> usuarios = new HashSet<>();

    public Mensaje() {}

    public Mensaje(String asunto, String cuerpo, Date fechaEnvio) {
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.fechaEnvio = fechaEnvio;
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
