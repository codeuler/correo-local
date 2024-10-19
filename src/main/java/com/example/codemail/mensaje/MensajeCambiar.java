package com.example.codemail.mensaje;

public record MensajeCambiar(
        Long idMesajeCambiar,
        Integer idFolderOrigen,
        Integer idFolderDestino
) {
}
