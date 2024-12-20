package com.example.codemail.carpeta;

import com.example.codemail.mensaje.MensajeNoExisteExcepcion;
import com.example.codemail.mensaje.MensajePerteneceCarpetaOrigenExcepcion;
import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteExcepcion;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/carpetas")
public class ControladorCarpeta {
    private final ServicioCarpeta servicioCarpeta;

    public ControladorCarpeta(ServicioCarpeta servicioCarpeta) {
        this.servicioCarpeta = servicioCarpeta;
    }

    @GetMapping
    public ResponseEntity<List<CarpetaRespuesta>> obtenerTodas(@AuthenticationPrincipal Usuario usuario) {
        return servicioCarpeta.getAll(usuario);
    }

    @PostMapping
    public ResponseEntity<String> crearFolder(@AuthenticationPrincipal Usuario usuario,
                                              @RequestBody @Validated CarpetaAGuardar carpetaAGuardar
    ) throws FolderYaExisteException {
        return servicioCarpeta.crearFolder(usuario, carpetaAGuardar);
    }

    @PutMapping("/{idCarpeta}")
    public ResponseEntity<CarpetaRespuesta> actualizarFolder(@AuthenticationPrincipal Usuario usuario,
                                                             @PathVariable Integer idCarpeta,
                                                             @Validated @RequestBody CarpetaAGuardar carpetaAGuardar
    ) throws FolderYaExisteException, CarpetaNoExisteExcepcion {
        return servicioCarpeta.actualizarFolder(usuario, idCarpeta, carpetaAGuardar);
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<CarpetaRespuesta> eliminarFolder(@AuthenticationPrincipal Usuario usuario,
                                                           @PathVariable Integer folderId
    ) throws CarpetaNoExisteExcepcion, MensajePropietarioNoExisteExcepcion, CarpetaImposibleEliminarExcepcion {
        return servicioCarpeta.eliminarFolder(usuario, folderId);
    }

    @GetMapping("/nombre/{nombreCarpeta}")
    public ResponseEntity<CarpetaRespuesta> obtenerFolder(@PathVariable String nombreCarpeta,
                                                          @AuthenticationPrincipal Usuario usuario
    ) throws CarpetaNoExisteExcepcion {
        return servicioCarpeta.buscarIdFolder(nombreCarpeta, usuario);
    }

    @DeleteMapping("{idCarpeta}/mensaje/{idMensaje}")
    public ResponseEntity<String> eliminarFolder(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer idCarpeta,
            @PathVariable Long idMensaje
    ) throws CarpetaNoExisteExcepcion, MensajePerteneceCarpetaOrigenExcepcion, MensajePropietarioNoExisteExcepcion, MensajeNoExisteExcepcion {
        return servicioCarpeta.eliminarMensajeFolder(idCarpeta, idMensaje, usuario);
    }


}
