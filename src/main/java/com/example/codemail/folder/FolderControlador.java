package com.example.codemail.folder;

import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/folders")
public class FolderControlador implements RequestTokenExtractor {
    private final FolderRepository folderRepository;
    private final FolderService folderService;
    private final UsuarioService usuarioService;

    public FolderControlador(FolderRepository folderRepository, FolderService folderService, UsuarioService usuarioService) {
        this.folderRepository = folderRepository;
        this.folderService = folderService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/obtener/todos")
    public ResponseEntity<?> obtenerTodos(HttpServletRequest request) {
        return folderService.getTodos(request);
    }
}
