package com.example.codemail.mensaje;

import com.example.codemail.folder.Folder;
import com.example.codemail.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MensajeMapper {
    public Mensaje toMensaje(MensajeEnviado mensajeEnviado, Usuario usuario, Set<Folder> folder) {
        return new Mensaje(
                mensajeEnviado.asunto(),
                mensajeEnviado.cuerpo(),
                mensajeEnviado.fecha(),
                folder,
                usuario
        );
    }
}
