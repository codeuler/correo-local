package com.example.codemail.mensaje;

import com.example.codemail.folder.*;
import com.example.codemail.mensajepropietario.*;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioCorreoNoValidoException;
import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MensajeService {
    private final FolderRepository folderRepository;
    private final MensajeRepository mensajeRepository;
    private final MensajeMapper mensajeMapper;
    private final MensajePropietarioService mensajePropietarioService;
    private final MensajePropietarioRepository mensajePropietarioRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajeService(UsuarioRepository usuarioRepository, FolderRepository folderRepository,
                          MensajeRepository mensajeRepository, MensajeMapper mensajeMapper,
                          MensajePropietarioService mensajePropietarioService,
                          MensajePropietarioRepository mensajePropietarioRepository) {
        this.folderRepository = folderRepository;
        this.mensajeRepository = mensajeRepository;
        this.mensajeMapper = mensajeMapper;
        this.mensajePropietarioService = mensajePropietarioService;
        this.mensajePropietarioRepository = mensajePropietarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ResponseEntity<String> enviarMensaje(MensajeEnviado mensajeEnviado, Usuario usuario)
            throws FolderNoExisteException, UsuarioCorreoNoValidoException {
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
        Folder folder = getFolder(usuario, CarpetasDefecto.ENVIADOS.getNombreCarpeta()).orElseThrow(() -> new FolderNoExisteException("No Existe la carpeta de 'Enviados'"));
        //En caso de que no exista ningún correo de destinatario
        if (destinatarios.isEmpty()) {
            throw new UsuarioCorreoNoValidoException("La o las direcciones de correo que se ingresaron no son válidas");
        }
        Mensaje mensaje = mensajeMapper.toMensaje(mensajeEnviado, usuario, folderEntrada);
        // Agregar el mensaje a cada folder
        folderEntrada.forEach(carpeta -> carpeta.getMensajes().add(mensaje));
        // Agregar el mensaje al folder enviados del dueño
        folder.getMensajes().add(mensaje);
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
                .forEach(user -> mensajePropietarioService.
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

    public ResponseEntity<String> cambiarFolder(MensajeCambiar mensajeCambiar)
            throws MensajeNoExisteException, MensajeErrorCambioFolderException {
        Mensaje mensaje = mensajeRepository.findById(mensajeCambiar.idMesajeCambiar()).orElseThrow(() -> new MensajeNoExisteException("El mensaje no existe"));
        Folder folderBase = folderRepository.findById(mensajeCambiar.idFolderOrigen()).orElseThrow(() -> new MensajeNoExisteException("El folder " + mensajeCambiar.idFolderOrigen() + " no existe"));
        Folder folderCambio = folderRepository.findById(mensajeCambiar.idFolderDestino()).orElseThrow(() -> new MensajeNoExisteException("El folder " + mensajeCambiar.idFolderDestino() + " no existe"));
        if (folderCambio.getNombre().equals(CarpetasDefecto.ENTRADA.toString()) || folderCambio.getNombre().equals(CarpetasDefecto.ENVIADOS.toString()) || folderCambio.getId().equals(folderBase.getId())) {
            throw new MensajeErrorCambioFolderException("No se puede cambiar a carpeta: Entrada, Enviados o sí mismo");
        }
        Usuario usuario = folderBase.getPropietario();
        if (mensajePropietarioRepository.findByUsuarioAndMensaje(usuario, mensaje).orElseThrow().getRolMensajePropietario().equals(RolMensajePropietario.AMBOS) && (folderBase.getNombre().equals(CarpetasDefecto.ENTRADA.toString()) || folderBase.getNombre().equals(CarpetasDefecto.ENVIADOS.toString()))) {
            // Se encuentra la bandeja de entrada y enviados del usuario, desvicular el mensaje de los folders y viceversa
            usuario.getFolders().stream()
                    .filter(folder -> folder.getNombre().equals(CarpetasDefecto.ENTRADA.toString()) || folder.getNombre().equals(CarpetasDefecto.ENVIADOS.toString()))
                    .forEach(folder -> FolderService.desvincularMensajeFolder(mensaje, folder));
        } else {
            // Se desvincula del folder anterior el mensaje
            // Se desvincula del mensaje el folder anterior
            FolderService.desvincularMensajeFolder(mensaje, folderBase);
        }
        // Se vincula al folder nuevo el mensaje
        // Se vincula al mensaje el nuevo folder
        FolderService.vincularMensajeFolder(mensaje, folderCambio);

        mensajeRepository.save(mensaje);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    private Optional<Folder> getFolder(Usuario usuario, String nombre) {
        return folderRepository
                .findByNombreAndPropietario(nombre, usuario);
    }

    public ResponseEntity<String> validarFolder(Integer mensajeId, Usuario usuario) {
        return mensajeRepository.findByIdAndAndUsuario(mensajeId, usuario).filter(
                mensaje -> (
                        mensaje.getFolder().stream().anyMatch(
                                folder -> Arrays.asList(CarpetasDefecto.ENTRADA.toString(), CarpetasDefecto.ENVIADOS.toString())
                                        .contains(folder.getNombre()) && folder.getPropietario().getId().equals(usuario.getId())
                        )
                )
        ).map(mensaje -> ResponseEntity.status(HttpStatus.OK).body("Pertenece a la Carpeta de Entrada/Enviados")).orElseGet(
                () -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Pertenece a la Carpeta de Entrada/Enviados"));
    }

    public ResponseEntity<String> eliminarMensajeFolder(MensajeEliminarFolder mensajeEliminarFolder, Usuario usuario)
            throws FolderNoExisteException, MensajePerteneceCarpetaOrigenException, MensajeNoExisteException, MensajePropietarioNoExisteException {
        // Verificar si la carpeta existe
        Folder folder = folderRepository.findByIdAndPropietario(mensajeEliminarFolder.folderId(), usuario).orElseThrow(() -> new FolderNoExisteException("No existe la carpeta buscada"));

        // Verificar si la carpeta no es de entrada o enviados
        if (Arrays.asList(CarpetasDefecto.ENTRADA.getNombreCarpeta(), CarpetasDefecto.ENVIADOS.getNombreCarpeta()).contains(folder.getNombre())) {
            throw new MensajePerteneceCarpetaOrigenException("El mensaje pertenece a la carpeta Entrada o enviados");
        }

        // Verificar si el mensaje existe
        Mensaje mensaje = mensajeRepository.findById(mensajeEliminarFolder.mensajeId()).orElseThrow(() -> new MensajeNoExisteException("El mensaje con id " + mensajeEliminarFolder.mensajeId() + " no existe"));

        // Verificar si el mensaje corresponde a un envio o recibido del usuario
        MensajePropietario MensajePropietario = usuario.getMensajesDestinatario().stream().filter(mensajeDestinatario -> mensajeDestinatario.getMensaje().getId().equals(mensajeEliminarFolder.mensajeId())).findFirst().orElseThrow(() -> new MensajePropietarioNoExisteException("No Existe una relación entre el mensaje y un usuario"));
        Folder folderEntrada = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENTRADA.getNombreCarpeta(), usuario).orElseThrow(() -> new FolderNoExisteException("No existe la carpeta 'Entrada'"));
        Folder folderEnviados = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENVIADOS.getNombreCarpeta(), usuario).orElseThrow(() -> new FolderNoExisteException("No existe la carpeta 'Enviados'"));
        FolderService.desvincularMensajeFolder(mensaje, folder);

        // Devolver el mensaje a su carpeta de origen
        switch (MensajePropietario.getRolMensajePropietario()) {
            case REMITENTE -> FolderService.vincularMensajeFolder(mensaje, folderEnviados);
            case DESTINATARIO -> FolderService.vincularMensajeFolder(mensaje, folderEntrada);
            case AMBOS -> {
                FolderService.vincularMensajeFolder(mensaje, folderEnviados);
                FolderService.vincularMensajeFolder(mensaje, folderEntrada);
            }
            default -> {
            }
        }
        mensajeRepository.save(mensaje);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<String> eliminarMensaje(MensajeEliminar mensajeEliminar, Usuario usuario)
            throws MensajeNoExisteException {
        // Verificar si el mensaje existe
        Mensaje mensaje = mensajeRepository.findById(mensajeEliminar.mensajeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El mensaje no existe"));
        // Lista para agrupar las carpetas en caso de que un mensaje este la bandeja de entrada como la de salida
        Set<Folder> folders = new HashSet<>();
        mensaje.getFolder().forEach(folder -> {
            if (folder.getPropietario().getId().equals(usuario.getId())) {
                folders.add(folder);
            }
        });
        if (folders.stream().noneMatch(folder -> folder.getMensajes().contains(mensaje))) {
            throw new MensajeNoExisteException("El mensaje con id " + mensajeEliminar.mensajeId() + " no pertenece al usuario");
        }
        // Desvincular el mensaje de la carpeta y viceversa
        folders.forEach(folder -> FolderService.desvincularMensajeFolder(mensaje, folder));
        mensajeRepository.save(mensaje);
        // Eliminar el registro que relaciona un mensaje con sus destinatario
        mensajePropietarioRepository.delete(mensaje.getMensajeDestinatario().stream()
                .filter(mensajesDestinatario -> mensajesDestinatario.getMensaje().getId().equals(mensaje.getId()) && mensajesDestinatario.getUsuario().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow(() -> new MensajeNoExisteException("El mensaje no existe")));
        // Verificar si la carpeta actualmente pertenece a alguna carpeta de lo contrario será eliminado completamente de la base de datos
        if (mensaje.getFolder().isEmpty()) {
            mensajeRepository.delete(mensaje);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
