package com.example.codemail.carpeta;

import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteExcepcion;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/folders")
public class ControladorCarpeta {
    private final ServicioCarpeta servicioCarpeta;

    public ControladorCarpeta(ServicioCarpeta servicioCarpeta) {
        this.servicioCarpeta = servicioCarpeta;
    }

    @GetMapping("/obtener/todos")
    public ResponseEntity<List<CarpetaRespuesta>> obtenerTodos(@AuthenticationPrincipal Usuario usuario) {
        return servicioCarpeta.getAll(usuario);
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearFolder(@AuthenticationPrincipal Usuario usuario, @RequestBody @Validated CarpetaAGuardar carpetaAGuardar) throws FolderYaExisteException {
        return servicioCarpeta.crearFolder(usuario, carpetaAGuardar);
    }

    @PutMapping("/{idCarpeta}/actualizar")
    public ResponseEntity<CarpetaRespuesta> actualizarFolder(@AuthenticationPrincipal Usuario usuario,
                                                             @PathVariable Integer idCarpeta,
                                                             @Validated @RequestBody CarpetaAGuardar carpetaAGuardar
    ) throws FolderYaExisteException, CarpetaNoExisteExcepcion {
        return servicioCarpeta.actualizarFolder(usuario, idCarpeta, carpetaAGuardar);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<CarpetaRespuesta> eliminarFolder(@AuthenticationPrincipal Usuario usuario,
                                                           @RequestBody @Validated CarpetaAEliminar carpetaAEliminar
    ) throws CarpetaNoExisteExcepcion, MensajePropietarioNoExisteExcepcion, CarpetaImposibleEliminarExcepcion {
        return servicioCarpeta.eliminarFolder(usuario, carpetaAEliminar);
    }

    @GetMapping("/{nombreCarpeta}/id")
    public ResponseEntity<CarpetaRespuesta> obtenerFolder(@PathVariable String nombreCarpeta,
                                                          @AuthenticationPrincipal Usuario usuario
    ) throws CarpetaNoExisteExcepcion {
        return servicioCarpeta.buscarIdFolder(nombreCarpeta, usuario);
    }

}
