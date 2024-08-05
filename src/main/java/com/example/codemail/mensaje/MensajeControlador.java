package com.example.codemail.mensaje;

import com.example.codemail.Jwt.RequestTokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mensajes")
public class MensajeControlador implements RequestTokenExtractor {
    private final MensajeService mensajeService;

    public MensajeControlador(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> enviarMensaje(
            @RequestBody MensajeEnviado mensajeEnviado,
            HttpServletRequest request
    ) {
        return mensajeService.enviarMensaje(mensajeEnviado, request);
    }
}
