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
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Usuario usuario = getUsuario(request);

        // Encontrar todos los usuarios que tengan por id el correo que se ha enviado en MensajeEnviado
        Set<Usuario> destinatarios = mensajeEnviado
                .correoDestinatarios()
                .stream()
                .map(correo -> usuarioRepository.findByEmail(correo).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Hallar el folder Entrada del usuario
        Folder folder = getFolder(usuario,"Entrada");

        if (destinatarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El/los correos no son válidos");
        } else if (folder == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder no existe");
        } else {
            mensajeRepository.save(mensajeMapper.toMensaje(mensajeEnviado,usuario,folder,destinatarios));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    public ResponseEntity<?> obtenerMensajes(HttpServletRequest request, String nombreFolder) {
        Usuario usuario = getUsuario(request);
        Folder carpeta = usuario.getFolders()
                .stream()
                .filter(folder -> folder.getNombre().equals(nombreFolder))
                .toList()
                .get(0);
        if (carpeta == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder " + nombreFolder + " no existe");
        } else {
            return ResponseEntity.ok(carpeta.getMensajes());
        }
    }



    private Folder getFolder(Usuario usuario,String nombre) {
        return folderRepository
                .findByNombreAndPropietario(nombre, usuario)
                .orElse(null);
    }

    private Usuario getUsuario(HttpServletRequest request) {
        //Buscar el username del usuario que envio la petición
        String username = jwtService.getUsernameFromToken(getTokenFromRequest(request));
        return usuarioRepository.findByEmail(username).orElse(null);
    }
}
