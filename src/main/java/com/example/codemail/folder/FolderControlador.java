package com.example.codemail.folder;

import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/folders")
public class FolderControlador implements RequestTokenExtractor {
    private final FolderService folderService;

    public FolderControlador(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/obtener/todos")
    public ResponseEntity<?> obtenerTodos(HttpServletRequest request) {
        return folderService.getTodos(request);
    }

    @PostMapping("crear/{nombreFolder}")
    public ResponseEntity<?> crearFolder (HttpServletRequest request, @PathVariable String nombreFolder) {
        return folderService.crearFolder(request, nombreFolder);
    }

}
