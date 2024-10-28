package com.example.codemail.folder;

import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteException;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/folders")
public class FolderControlador {
    private final FolderService folderService;

    public FolderControlador(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/obtener/todos")
    public ResponseEntity<Set<FolderRespuesta>> obtenerTodos(@AuthenticationPrincipal Usuario usuario) {
        return folderService.getAll(usuario);
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearFolder(@AuthenticationPrincipal Usuario usuario, @RequestBody @Validated FolderGuardar folderGuardar) throws FolderYaExisteException {
        return folderService.crearFolder(usuario, folderGuardar);
    }

    @PutMapping("/{idCarpeta}/actualizar")
    public ResponseEntity<FolderRespuesta> actualizarFolder(@AuthenticationPrincipal Usuario usuario,
                                                            @PathVariable Integer idCarpeta,
                                                            @Validated @RequestBody FolderGuardar folderGuardar) throws FolderYaExisteException, FolderNoExisteException {
        return folderService.actualizarFolder(usuario, idCarpeta, folderGuardar);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<FolderRespuesta> eliminarFolder(@AuthenticationPrincipal Usuario usuario,
                                                          @RequestBody @Validated FolderEliminar folderEliminar) throws FolderNoExisteException, MensajePropietarioNoExisteException, FolderImposibleEliminarException {
        return folderService.eliminarFolder(usuario, folderEliminar);
    }

}
