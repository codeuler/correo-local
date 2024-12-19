package com.example.codemail.carpeta;

import jakarta.validation.constraints.NotNull;

public record CarpetaAEliminar(
        @NotNull(message = "El id no puede ser nulo")
        Integer folderId
) {
}
