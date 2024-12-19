package com.example.codemail.mensaje;

public record MensajeAActualizar(
        Long idMesajeCambiar,
        Integer idFolderOrigen,
        Integer idFolderDestino
) {
}
