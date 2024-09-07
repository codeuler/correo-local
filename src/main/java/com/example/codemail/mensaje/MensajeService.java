package com.example.codemail.mensaje;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.folder.CarpetasDefecto;
import com.example.codemail.folder.Folder;
import com.example.codemail.folder.FolderRepository;
import com.example.codemail.mensajepropietario.MensajePropietario;
import com.example.codemail.mensajepropietario.MensajePropietarioService;
import com.example.codemail.mensajepropietario.RolMensajePropietario;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import com.example.codemail.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MensajeService extends UsuarioService implements RequestTokenExtractor {
    private final FolderRepository folderRepository;
    private final MensajeRepository mensajeRepository;
    private final MensajeMapper mensajeMapper;
    private final MensajePropietarioService mensajePropietarioService;

    public MensajeService(JwtService jwtService, UsuarioRepository usuarioRepository, FolderRepository folderRepository,
                          MensajeRepository mensajeRepository, MensajeMapper mensajeMapper,
                          MensajePropietarioService mensajePropietarioService) {
        super(jwtService, usuarioRepository);
        this.folderRepository = folderRepository;
        this.mensajeRepository = mensajeRepository;
        this.mensajeMapper = mensajeMapper;
        this.mensajePropietarioService = mensajePropietarioService;
    }

    public ResponseEntity<?> enviarMensaje(MensajeEnviado mensajeEnviado, HttpServletRequest request) {
        Usuario usuario = getUsuario(request);
        // Encontrar todos los usuarios que tengan por id el correo que se ha enviado en MensajeEnviado
        Set<Usuario> destinatarios = mensajeEnviado
                .correoDestinatarios()
                .stream()
                .map(usuarioRepository::findByEmail)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        // Buscar todos los folder de entrada de los detinarios
        Set<Folder> folderEntrada = destinatarios
                .stream()
                .map(user -> getFolder(user, CarpetasDefecto.ENTRADA.getNombreCarpeta()))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        // Hallar el folder Envio del usuario
        Optional<Folder> folder = getFolder(usuario, CarpetasDefecto.ENVIADOS.getNombreCarpeta());
        //En caso de que no exista ningún correo de destinatario
        if (destinatarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El/los correos no son válidos");
        } else if (folder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder no existe");
        }
        Mensaje mensaje = mensajeMapper.toMensaje(mensajeEnviado, usuario, folderEntrada);
        // Agregar el mensaje a cada folder
        folderEntrada.forEach(carpeta -> carpeta.getMensajes().add(mensaje));
        // Agregar el mensaje al folder enviados del dueño
        folder.get().getMensajes().add(mensaje);
        // Guardar el mensaje en la base de datos
        mensajeRepository.save(mensaje);
        // Revisar se se hizo un autoenvio
        boolean existeAutoenvio = destinatarios.stream()
                .anyMatch(
                        user -> user.getId().equals(usuario.getId())
                );
        // Guardar cada relación en la base de datos
        destinatarios.stream()
                // Eliminar al usuario que realizo el autoenvio
                .filter(user -> !user.getId().equals(usuario.getId()))
                .forEach( user -> mensajePropietarioService.
                        guardarMensajePropietario(
                                new MensajePropietario(user, mensaje, false, RolMensajePropietario.DESTINATARIO)
                        )
                );
        // Se agrega porque se va a guardar dentro del folder de enviados, si el usuario que envía se hizo un autoenvio, es marcado con el rol de ambos
        mensajePropietarioService.guardarMensajePropietario(
                new MensajePropietario(usuario, mensaje, false, (existeAutoenvio) ? RolMensajePropietario.AMBOS : RolMensajePropietario.REMITENTE)
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private Optional<Folder> getFolder(Usuario usuario,String nombre) {
        return folderRepository
                .findByNombreAndPropietario(nombre, usuario);
    }



}
