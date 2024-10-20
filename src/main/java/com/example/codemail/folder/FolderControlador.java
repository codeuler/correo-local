package com.example.codemail.folder;

import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.errores.ManejadorDeErroresHttp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/folders")
public class FolderControlador implements RequestTokenExtractor, ManejadorDeErroresHttp {
    private final FolderService folderService;

    public FolderControlador(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/obtener/todos")
    public ResponseEntity<?> obtenerTodos(HttpServletRequest request) {
        return folderService.getAll(request);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearFolder (HttpServletRequest request, @RequestBody @Validated FolderGuardar folderGuardar) {
        return folderService.crearFolder(request, folderGuardar);
    }

    @PutMapping("/{nombreCarpeta}/actualizar")
    public ResponseEntity<?> actualizarFolder (HttpServletRequest request,
                                               @PathVariable String nombreCarpeta,
                                               @Validated @RequestBody FolderGuardar folderGuardar) {
        return folderService.actualizarFolder(request, nombreCarpeta, folderGuardar);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarFolder(HttpServletRequest request,
                                            @RequestBody @Validated FolderEliminar folderEliminar) {
        return folderService.eliminarFolder(request, folderEliminar);
    }

}
