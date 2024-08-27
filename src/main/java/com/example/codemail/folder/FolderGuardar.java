package com.example.codemail.folder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record FolderGuardar(
        @NotEmpty
        @Size(min = 5, max = 255,message = "El nombre debe tener una tamaño entre 5 y 255 carácteres")
        String nombre
) {
}
