package com.example.codemail.mensaje;

import com.example.codemail.carpeta.Carpeta;
import com.example.codemail.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class MensajeMapeador {
    public Mensaje toMensaje(MensajeEnviado mensajeEnviado, Usuario usuario, Set<Carpeta> carpeta) {
        return new Mensaje(
                mensajeEnviado.asunto(),
                mensajeEnviado.cuerpo(),
                new Date(),
                carpeta,
                usuario
        );
    }
}
