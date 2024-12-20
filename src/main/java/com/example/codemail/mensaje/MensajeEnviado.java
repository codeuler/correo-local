package com.example.codemail.mensaje;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record MensajeEnviado(
        @NotEmpty(message = "Debe haber al menos un destinatario")
        Set<String> correoDestinatarios,
        @NotEmpty(message = "El asunto no puedes estar vacío")
        String asunto,
        @NotEmpty(message = "El Cuerpo no puedes estar vacío")
        String cuerpo
) {
}
