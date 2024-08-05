package com.example.codemail.folder;

import com.example.codemail.usuario.Usuario;

public record FolderGuardar(
        String nombre,
        Usuario propietario
) {
}
