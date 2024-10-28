package com.example.codemail.mensajepropietario;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.folder.Folder;
import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensaje.MensajeNoExisteException;
import com.example.codemail.mensaje.MensajeRepository;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MensajePropietarioService {
    private final MensajePropietarioRepository mensajePropietarioRepository;
    private final MensajePropietarioMapper mensajePropietarioMapper;
    private final MensajeRepository mensajeRepository;

    public MensajePropietarioService(MensajePropietarioRepository mensajePropietarioRepository, MensajePropietarioMapper mensajePropietarioMapper, JwtService jwtService, UsuarioRepository usuarioRepository, MensajeRepository mensajeRepository) {
        this.mensajePropietarioRepository = mensajePropietarioRepository;
        this.mensajePropietarioMapper = mensajePropietarioMapper;
        this.mensajeRepository = mensajeRepository;
    }

    public void guardarMensajePropietario(MensajePropietario mensajePropietario) {
        mensajePropietarioRepository.save(mensajePropietario);
    }

    public ResponseEntity<Set<MensajePropietarioEntrega>> obtenerMensajes(Usuario usuario, Integer folderId) throws MensajeNoExisteException {
        //Obtener el folder especifico del usuario
        Folder carpeta = usuario.getFolders()
                .stream()
                .filter(folder -> folder.getId().equals(folderId))
                .findFirst()
                .orElseThrow(() -> new MensajeNoExisteException("El folder con id" + folderId + " no existe"));

        return ResponseEntity.ok(carpeta
                .getMensajes()
                .stream()
                .map(mensaje -> mensajePropietarioRepository.findByUsuarioAndMensaje(usuario, mensaje))
                .flatMap(Optional::stream)
                .map(mensajePropietarioMapper::toMensajePropietarioEntrega)
                .collect(Collectors.toSet())
        );
    }

    public ResponseEntity<String> revisarMensaje(Usuario usuario, MensajePropietarioRevisar mensajePropietarioRevisar) throws MensajeNoExisteException, MensajePropietarioNoExisteException {
        // Revisar que el mensaje exista
        Mensaje mensaje = mensajeRepository.findById(mensajePropietarioRevisar.mensajeId()).orElseThrow(() -> new MensajeNoExisteException("No existe el mensaje con id " + mensajePropietarioRevisar.mensajeId()));
        // Revisar que el mensaje tenga relación con algún destinatario
        MensajePropietario mensajePropietarioEntregar = mensajePropietarioRepository.findByUsuarioAndMensaje(usuario, mensaje).orElseThrow(() -> new MensajePropietarioNoExisteException("No se ha encontrado dicho mensaje asociado al usuario"));
        mensajePropietarioEntregar.setRevisado(true);
        mensajePropietarioRepository.save(mensajePropietarioEntregar);
        return ResponseEntity.ok("Mensaje revisado con exito");
    }
}
