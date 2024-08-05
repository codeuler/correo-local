package com.example.codemail.mensaje;

import java.util.Date;
import java.util.Set;

public record MensajeEnviado (
    Set<String> correoDestinatarios,
    String asunto,
    String cuerpo,
    Date fecha
) {
}
