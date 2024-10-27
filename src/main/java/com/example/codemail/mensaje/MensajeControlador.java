package com.example.codemail.mensaje;

import com.example.codemail.errores.ManejadorDeErroresHttp;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mensajes")
public class MensajeControlador implements ManejadorDeErroresHttp {
    private final MensajeService mensajeService;

    public MensajeControlador(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> enviarMensaje(
            @RequestBody MensajeEnviado mensajeEnviado,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return mensajeService.enviarMensaje(mensajeEnviado, usuario);
    }

    @PutMapping("/cambiarFolder")
    public ResponseEntity<?> cambiarFolder(
            @RequestBody MensajeCambiar mensajeCambiar
    ) {
        return mensajeService.cambiarFolder(mensajeCambiar);
    }

    @GetMapping("/{mensajeId}/validacionFolder")
    public ResponseEntity<?> validacionFolder(
            @PathVariable Integer mensajeId,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return mensajeService.validarFolder(mensajeId, usuario);
    }

    @DeleteMapping("/eliminar/folder")
    public ResponseEntity<?> eliminarFolder(@RequestBody MensajeEliminarFolder mensajeEliminarFolder, @AuthenticationPrincipal Usuario usuario) {
        return mensajeService.eliminarMensajeFolder(mensajeEliminarFolder, usuario);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarMensaje(@RequestBody MensajeEliminar mensajeEliminar, @AuthenticationPrincipal Usuario usuario) {
        return mensajeService.eliminarMensaje(mensajeEliminar, usuario);
    }
}
