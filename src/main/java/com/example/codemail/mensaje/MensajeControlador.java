package com.example.codemail.mensaje;

import com.example.codemail.carpeta.CarpetaNoExisteExcepcion;
import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteExcepcion;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.CorreoNoValidoExcepcion;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mensajes")
public class MensajeControlador {
    private final ServicioMensaje servicioMensaje;

    public MensajeControlador(ServicioMensaje servicioMensaje) {
        this.servicioMensaje = servicioMensaje;
    }

    @PostMapping
    public ResponseEntity<String> enviarMensaje(
            @RequestBody MensajeEnviado mensajeEnviado,
            @AuthenticationPrincipal Usuario usuario
    ) throws CorreoNoValidoExcepcion, CarpetaNoExisteExcepcion {
        return servicioMensaje.enviarMensaje(mensajeEnviado, usuario);
    }

    @PutMapping("/{idMensaje}/carpeta")
    public ResponseEntity<String> cambiarFolder(
            @RequestBody MensajeAActualizar mensajeAActualizar,
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idMensaje
    ) throws MensajeNoExisteExcepcion, ErrorCambioCarpetaExcepcion {
        return servicioMensaje.cambiarFolder(mensajeAActualizar, idMensaje, usuario);
    }

    @GetMapping("/{mensajeId}/es-entrada-o-enviados")
    public ResponseEntity<String> validacionFolder(
            @PathVariable Long mensajeId,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion {
        return servicioMensaje.validarFolder(mensajeId, usuario);
    }

    @DeleteMapping("/{idMensaje}")
    public ResponseEntity<String> eliminarMensaje(
            @PathVariable Long idMensaje,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion {
        return servicioMensaje.eliminarMensaje(idMensaje, usuario);
    }
}
