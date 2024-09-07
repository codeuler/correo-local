package com.example.codemail.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FolderGuardar(
        @NotBlank(message = "El mensaje no puede estar constituido únicamente por espacios en blanco")
        @Size(min = 5, max = 255,message = "El nombre debe tener una tamaño entre 5 y 255 carácteres")
        String nombre
) {
}
