package com.example.codemail.carpeta;

import com.example.codemail.usuario.Usuario;
import org.springframework.stereotype.Service;

@Service
public class CarpetaMapeador {
    public Carpeta toFolder(Usuario usuario, String nombre){
        return new Carpeta(nombre, usuario);
    }
    public CarpetaRespuesta toFolderRespuesta(Carpeta carpeta) {
        return new CarpetaRespuesta(carpeta.getId(), carpeta.getNombre());
    }
}
