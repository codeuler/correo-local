package com.example.codemail.mensajepropietario;

import com.example.codemail.folder.Folder;
import com.example.codemail.folder.FolderRepository;
import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensaje.MensajeNoExisteException;
import com.example.codemail.mensaje.MensajeRepository;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MensajePropietarioService {
    private final MensajePropietarioRepository mensajePropietarioRepository;
    private final MensajePropietarioMapper mensajePropietarioMapper;
    private final MensajeRepository mensajeRepository;
    private final FolderRepository folderRepository;

    public MensajePropietarioService(MensajePropietarioRepository mensajePropietarioRepository, MensajePropietarioMapper mensajePropietarioMapper, MensajeRepository mensajeRepository, FolderRepository folderRepository) {
        this.mensajePropietarioRepository = mensajePropietarioRepository;
        this.mensajePropietarioMapper = mensajePropietarioMapper;
        this.mensajeRepository = mensajeRepository;
        this.folderRepository = folderRepository;
    }

    public void guardarMensajePropietario(MensajePropietario mensajePropietario) {
        mensajePropietarioRepository.save(mensajePropietario);
    }

    public ResponseEntity<List<MensajePropietarioEntrega>> obtenerMensajes(Usuario usuario, Integer folderId) throws MensajeNoExisteException {
        //Obtener el folder especifico del usuario
        Folder carpeta = folderRepository.findById(folderId)
                .orElseThrow(() -> new MensajeNoExisteException("El folder con id" + folderId + " no existe"));

        return ResponseEntity.ok(mensajeRepository.findAllByUsuarioAndFolder(usuario,Set.of(carpeta))
                .stream()
                .map(mensaje -> mensajePropietarioRepository.findByUsuarioAndMensaje(usuario, mensaje))
                .flatMap(Optional::stream)
                .map(mensajePropietarioMapper::toMensajePropietarioEntrega)
                .sorted((mensajePropitarioA, mensajePropitarioB) -> mensajePropitarioA.fechaEnvio().compareTo(mensajePropitarioB.fechaEnvio()) * -1)
                .collect(Collectors.toList())
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
