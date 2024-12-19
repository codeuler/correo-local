package com.example.codemail.mensaje;

import com.example.codemail.folder.Folder;
import com.example.codemail.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class MensajeMapeador {
    public Mensaje toMensaje(MensajeEnviado mensajeEnviado, Usuario usuario, Set<Folder> folder) {
        return new Mensaje(
                mensajeEnviado.asunto(),
                mensajeEnviado.cuerpo(),
                new Date(),
                folder,
                usuario
        );
    }
}
