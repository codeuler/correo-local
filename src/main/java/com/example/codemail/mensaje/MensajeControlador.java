package com.example.codemail.mensaje;

import com.example.codemail.errores.ManejadorDeErroresHttp;
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
public class MensajeControlador implements ManejadorDeErroresHttp {
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
            @RequestBody MensajeCambiar mensajeCambiar
    ) throws MensajeNoExisteException, MensajeErrorCambioFolderException {
        return mensajeService.cambiarFolder(mensajeCambiar);
    }

    @GetMapping("/{mensajeId}/validacionFolder")
    public ResponseEntity<String> validacionFolder(
            @PathVariable Integer mensajeId,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return mensajeService.validarFolder(mensajeId, usuario);
    }

    @DeleteMapping("/eliminar/folder")
    public ResponseEntity<String> eliminarFolder(
            @RequestBody MensajeEliminarFolder mensajeEliminarFolder,
            @AuthenticationPrincipal Usuario usuario
    ) throws FolderNoExisteException, MensajePerteneceCarpetaOrigenException, MensajePropietarioNoExisteException, MensajeNoExisteException {
        return mensajeService.eliminarMensajeFolder(mensajeEliminarFolder, usuario);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarMensaje(
            @RequestBody MensajeEliminar mensajeEliminar,
            @AuthenticationPrincipal Usuario usuario
    ) throws MensajeNoExisteException {
        return mensajeService.eliminarMensaje(mensajeEliminar, usuario);
    }
}
