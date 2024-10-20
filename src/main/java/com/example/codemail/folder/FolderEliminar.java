package com.example.codemail.folder;

import jakarta.validation.constraints.NotNull;

public record FolderEliminar(
        @NotNull(message = "El id no puede ser nulo")
        Integer folderId
) {
}
