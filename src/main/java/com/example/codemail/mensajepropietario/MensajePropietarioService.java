package com.example.codemail.mensajepropietario;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.folder.Folder;
import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensaje.MensajeRepository;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MensajePropietarioService implements RequestTokenExtractor {
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

    public ResponseEntity<?> obtenerMensajes(Usuario usuario, Integer folderId) {
        //Obtener el folder especifico del usuario
        Optional<Folder> carpetaOptional = usuario.getFolders()
                .stream()
                .filter(folder -> folder.getId().equals(folderId))
                .findFirst();

        if (carpetaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder con id" + folderId + " no existe");
        }

        Folder carpeta = carpetaOptional.get();
        return ResponseEntity.ok(carpeta
                .getMensajes()
                .stream()
                .map(mensaje -> mensajePropietarioRepository.findByUsuarioAndMensaje(usuario, mensaje))
                .flatMap(Optional::stream)
                .map(mensajePropietarioMapper::toMensajePropietarioEntrega)
                .collect(Collectors.toSet())
        );
    }

    public ResponseEntity<?> revisarMensaje(Usuario usuario, MensajePropietarioRevisar mensajePropietarioRevisar) {
        Optional<Mensaje> mensaje = mensajeRepository.findById(mensajePropietarioRevisar.mensajeId());
        if (mensaje.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se ha encontrado dicho mensaje");
        }
        Optional<MensajePropietario> mensajePropietario = mensajePropietarioRepository.findByUsuarioAndMensaje(usuario, mensaje.get());
        if (mensajePropietario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se ha encontrado dicho mensaje asociado al usuario");
        }
        MensajePropietario mensajePropietarioEntregar = mensajePropietario.get();
        mensajePropietarioEntregar.setRevisado(true);
        mensajePropietarioRepository.save(mensajePropietarioEntregar);
        return ResponseEntity.ok("Mensaje revisado con exito");
    }
}
