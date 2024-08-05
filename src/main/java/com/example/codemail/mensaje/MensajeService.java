package com.example.codemail.mensaje;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.folder.Folder;
import com.example.codemail.folder.FolderRepository;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MensajeService implements RequestTokenExtractor {
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final FolderRepository folderRepository;
    private final MensajeRepository mensajeRepository;
    private final MensajeMapper mensajeMapper;

    public MensajeService(JwtService jwtService, UsuarioRepository usuarioRepository, FolderRepository folderRepository, MensajeRepository mensajeRepository, MensajeMapper mensajeMapper) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.folderRepository = folderRepository;
        this.mensajeRepository = mensajeRepository;
        this.mensajeMapper = mensajeMapper;
    }

    public ResponseEntity<?> enviarMensaje(MensajeEnviado mensajeEnviado, HttpServletRequest request) {
        //Buscar el username del usuario que envio la petición
        String username = jwtService.getUsernameFromToken(getTokenFromRequest(request));
        Usuario usuario = usuarioRepository.findByEmail(username).orElse(null);

        // Encontrar todos los usuarios que tengan por id el correo que se ha enviado en MensajeEnviado
        Set<Usuario> destinatarios = mensajeEnviado
                .correoDestinatarios()
                .stream()
                .map(correo -> usuarioRepository.findByEmail(correo).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Hallar el folder Entrada del usuario
        Folder folder = folderRepository
                .findByNombreAndPropietario("Entrada",usuario)
                .orElse(null);

        if (destinatarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El/los correos no son válidos");
        } else if (folder == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder no existe");
        } else {
            mensajeRepository.save(mensajeMapper.toMensaje(mensajeEnviado,usuario,folder,destinatarios));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }
}
