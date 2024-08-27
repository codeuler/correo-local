package com.example.codemail.folder;

import com.example.codemail.usuario.Usuario;
import org.springframework.stereotype.Service;

@Service
public class FolderMapper {
    public Folder toFolder(Usuario usuario, String nombre){
        return new Folder(nombre, usuario);
    }
    public FolderRespuesta toFolderRespuesta(Folder folder) {
        return new FolderRespuesta(folder.getId(), folder.getNombre());
    }
}
