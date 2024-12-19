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

    @PostMapping("/crear")
    public ResponseEntity<String> enviarMensaje(
            @RequestBody MensajeEnviado mensajeEnviado,
            @AuthenticationPrincipal Usuario usuario
    ) throws CorreoNoValidoExcepcion, CarpetaNoExisteExcepcion {
        return servicioMensaje.enviarMensaje(mensajeEnviado, usuario);
    }

    @PutMapping("/cambiarFolder")
    public ResponseEntity<String> cambiarFolder(
            @RequestBody MensajeAActualizar mensajeAActualizar,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion, ErrorCambioCarpetaExcepcion {
        return servicioMensaje.cambiarFolder(mensajeAActualizar, usuario);
    }

    @GetMapping("/{mensajeId}/validacionFolder")
    public ResponseEntity<String> validacionFolder(
            @PathVariable Long mensajeId,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion {
        return servicioMensaje.validarFolder(mensajeId, usuario);
    }

    @DeleteMapping("/eliminar/folder")
    public ResponseEntity<String> eliminarFolder(
            @RequestBody MensajeAEliminarDeCarpeta mensajeAEliminarDeCarpeta,
            @AuthenticationPrincipal Usuario usuario
    ) throws CarpetaNoExisteExcepcion, MensajePerteneceCarpetaOrigenExcepcion, MensajePropietarioNoExisteExcepcion, MensajeNoExisteExcepcion {
        return servicioMensaje.eliminarMensajeFolder(mensajeAEliminarDeCarpeta, usuario);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarMensaje(
            @RequestBody MensajeAEliminar mensajeAEliminar,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion {
        return servicioMensaje.eliminarMensaje(mensajeAEliminar, usuario);
    }
}
