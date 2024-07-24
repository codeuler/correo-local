package com.example.codemail.mensaje;

import com.example.codemail.usuario.Usuario;
import jakarta.persistence.*;

import java.util.Date;

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
    @JoinColumn(name="id")
    private Usuario usuario;

    public Mensaje() {}

    public Mensaje(Long id, String asunto, String cuerpo, Date fechaEnvio) {
        this.id = id;
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
