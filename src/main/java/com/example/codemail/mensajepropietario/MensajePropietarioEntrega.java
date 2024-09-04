package com.example.codemail.mensajepropietario;

import java.util.Date;

public record MensajePropietarioEntrega(
        Long id,
        String asunto,
        String cuerpo,
        Date fechaEnvio,
        String emailRemitente,
        Boolean revisado
) {
}
