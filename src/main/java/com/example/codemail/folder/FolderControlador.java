package com.example.codemail.folder;

import com.example.codemail.errores.ManejadorDeErroresHttp;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/folders")
public class FolderControlador implements ManejadorDeErroresHttp {
    private final FolderService folderService;

    public FolderControlador(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/obtener/todos")
    public ResponseEntity<?> obtenerTodos(@AuthenticationPrincipal Usuario usuario) {
        return folderService.getAll(usuario);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearFolder (@AuthenticationPrincipal Usuario usuario, @RequestBody @Validated FolderGuardar folderGuardar) {
        return folderService.crearFolder(usuario, folderGuardar);
    }

    @PutMapping("/{idCarpeta}/actualizar")
    public ResponseEntity<?> actualizarFolder (@AuthenticationPrincipal Usuario usuario,
                                               @PathVariable Integer idCarpeta,
                                               @Validated @RequestBody FolderGuardar folderGuardar) {
        return folderService.actualizarFolder(usuario, idCarpeta, folderGuardar);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarFolder(@AuthenticationPrincipal Usuario usuario,
                                            @RequestBody @Validated FolderEliminar folderEliminar) {
        return folderService.eliminarFolder(usuario, folderEliminar);
    }

}
