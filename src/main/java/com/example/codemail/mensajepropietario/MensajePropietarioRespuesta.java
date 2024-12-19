package com.example.codemail.mensajepropietario;

import java.util.Date;

public record MensajePropietarioRespuesta(
        Long mensajeId,
        String asunto,
        String cuerpo,
        Date fechaEnvio,
        String emailRemitente,
        Boolean revisado
) {
}
