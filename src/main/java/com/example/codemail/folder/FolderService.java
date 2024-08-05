package com.example.codemail.folder;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import com.example.codemail.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FolderService implements RequestTokenExtractor {
    private final FolderRepository folderRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final FolderMapper folderMapper;

    public FolderService(FolderRepository folderRepository, UsuarioService usuarioService, JwtService jwtService, UsuarioRepository usuarioRepository, FolderMapper folderMapper) {
        this.folderRepository = folderRepository;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.folderMapper = folderMapper;
    }

    public ResponseEntity<?> getTodos(HttpServletRequest request) {
        Usuario usuario = getUsuario(request);
        Set<Folder> folders = usuario.getFolders();
        return ResponseEntity.ok(folders);
    }

    public ResponseEntity<?> crearFolder(HttpServletRequest request, String nombreFolder) {
        Usuario usuario = getUsuario(request);
        folderRepository.save(folderMapper.toFolder(usuario, nombreFolder));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public void crearFolder(Usuario usuario, String nombreFolder) {
        folderRepository.save(folderMapper.toFolder(usuario, nombreFolder));
    }

    private Usuario getUsuario(HttpServletRequest request) {
        //Buscar el username del usuario que envio la petición
        String username = jwtService.getUsernameFromToken(getTokenFromRequest(request));
        return usuarioRepository.findByEmail(username).orElse(null);
    }
}
