package com.example.codemail.mensaje;

import com.example.codemail.folder.FolderNoExisteException;
import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteException;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioCorreoNoValidoException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mensajes")
public class MensajeControlador {
    private final MensajeService mensajeService;

    public MensajeControlador(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> enviarMensaje(
            @RequestBody MensajeEnviado mensajeEnviado,
            @AuthenticationPrincipal Usuario usuario
    ) throws UsuarioCorreoNoValidoException, FolderNoExisteException {
        return mensajeService.enviarMensaje(mensajeEnviado, usuario);
    }

    @PutMapping("/cambiarFolder")
    public ResponseEntity<String> cambiarFolder(
            @RequestBody MensajeAActualizar mensajeAActualizar,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion, ErrorCambioCarpetaExcepcion {
        return mensajeService.cambiarFolder(mensajeAActualizar, usuario);
    }

    @GetMapping("/{mensajeId}/validacionFolder")
    public ResponseEntity<String> validacionFolder(
            @PathVariable Long mensajeId,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion {
        return mensajeService.validarFolder(mensajeId, usuario);
    }

    @DeleteMapping("/eliminar/folder")
    public ResponseEntity<String> eliminarFolder(
            @RequestBody MensajeAEliminarDeCarpeta mensajeAEliminarDeCarpeta,
            @AuthenticationPrincipal Usuario usuario
    ) throws FolderNoExisteException, MensajePerteneceCarpetaOrigenExcepcion, MensajePropietarioNoExisteException, MensajeNoExisteExcepcion {
        return mensajeService.eliminarMensajeFolder(mensajeAEliminarDeCarpeta, usuario);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarMensaje(
            @RequestBody MensajeAEliminar mensajeAEliminar,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteExcepcion {
        return mensajeService.eliminarMensaje(mensajeAEliminar, usuario);
    }
}
