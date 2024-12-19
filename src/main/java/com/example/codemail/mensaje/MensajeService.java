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
    private final RepositorioMensaje repositorioMensaje;
    private final MensajeMapeador mensajeMapeador;
    private final ServicioMensajePropietario servicioMensajePropietario;
    private final RepositorioMensajePropietario repositorioMensajePropietario;
    private final UsuarioRepository usuarioRepository;

    public MensajeService(UsuarioRepository usuarioRepository, FolderRepository folderRepository,
                          RepositorioMensaje repositorioMensaje, MensajeMapeador mensajeMapeador,
                          ServicioMensajePropietario servicioMensajePropietario,
                          RepositorioMensajePropietario repositorioMensajePropietario) {
        this.folderRepository = folderRepository;
        this.repositorioMensaje = repositorioMensaje;
        this.mensajeMapeador = mensajeMapeador;
        this.servicioMensajePropietario = servicioMensajePropietario;
        this.repositorioMensajePropietario = repositorioMensajePropietario;
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
        Mensaje mensaje = mensajeMapeador.toMensaje(mensajeEnviado, usuario, folderEntrada);
        // Agregar el mensaje a cada folder
        folderEntrada.forEach(carpeta -> carpeta.getMensajes().add(mensaje));
        // Agregar el mensaje al folder enviados del dueño
        folder.getMensajes().add(mensaje);
        // Guardar el mensaje en la base de datos
        repositorioMensaje.save(mensaje);
        // Revisar se se hizo un autoenvio
        boolean existeAutoenvio = destinatarios.stream()
                .anyMatch(
                        user -> user.getId().equals(usuario.getId())
                );
        // Guardar cada relación en la base de datos
        destinatarios.stream()
                // Eliminar al usuario que realizo el autoenvio
                .filter(user -> !user.getId().equals(usuario.getId()))
                .forEach(user -> servicioMensajePropietario.
                        guardarMensajePropietario(
                                new MensajePropietario(user, mensaje, false, RolMensajePropietario.DESTINATARIO)
                        )
                );
        // Se agrega porque se va a guardar dentro del folder de enviados, si el usuario que envía se hizo un autoenvio, es marcado con el rol de ambos
        servicioMensajePropietario.guardarMensajePropietario(
                new MensajePropietario(usuario, mensaje, false, (existeAutoenvio) ? RolMensajePropietario.AMBOS : RolMensajePropietario.REMITENTE)
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<String> cambiarFolder(MensajeAActualizar mensajeAActualizar, Usuario usuario)
            throws MensajeNoExisteExcepcion, ErrorCambioCarpetaExcepcion {
        Folder folderBase = folderRepository.findById(mensajeAActualizar.idFolderOrigen()).orElseThrow(() -> new MensajeNoExisteExcepcion("El folder " + mensajeAActualizar.idFolderOrigen() + " no existe"));
        Folder folderCambio = folderRepository.findById(mensajeAActualizar.idFolderDestino()).orElseThrow(() -> new MensajeNoExisteExcepcion("El folder " + mensajeAActualizar.idFolderDestino() + " no existe"));
        Mensaje mensaje = repositorioMensaje.findByIdAndFolder(mensajeAActualizar.idMesajeCambiar(), Set.of(folderBase)).orElseThrow(() -> new MensajeNoExisteExcepcion("El mensaje no existe"));
        if (folderCambio.getNombre().equals(CarpetasDefecto.ENTRADA.getNombreCarpeta()) || folderCambio.getNombre().equals(CarpetasDefecto.ENVIADOS.getNombreCarpeta()) || folderCambio.getId().equals(folderBase.getId())) {
            throw new ErrorCambioCarpetaExcepcion("No se puede cambiar a carpeta: Entrada, Enviados o sí mismo");
        }
        if (repositorioMensajePropietario.findByUsuarioAndMensaje(usuario, mensaje).orElseThrow().getRolMensajePropietario().equals(RolMensajePropietario.AMBOS) && (folderBase.getNombre().equals(CarpetasDefecto.ENTRADA.getNombreCarpeta()) || folderBase.getNombre().equals(CarpetasDefecto.ENVIADOS.getNombreCarpeta()))) {
            // Se encuentra la bandeja de entrada y enviados del usuario, desvicular el mensaje de los folders y viceversa
            folderRepository.findAllByPropietario(usuario).stream()
                    .filter(folder -> folder.getNombre().equals(CarpetasDefecto.ENTRADA.getNombreCarpeta()) || folder.getNombre().equals(CarpetasDefecto.ENVIADOS.getNombreCarpeta()))
                    .forEach(folder -> FolderService.desvincularMensajeFolder(mensaje, folder));
        } else {
            // Se desvincula del folder anterior el mensaje
            // Se desvincula del mensaje el folder anterior
            FolderService.desvincularMensajeFolder(mensaje, folderBase);
        }
        // Se vincula al folder nuevo el mensaje
        // Se vincula al mensaje el nuevo folder
        FolderService.vincularMensajeFolder(mensaje, folderCambio);

        repositorioMensaje.save(mensaje);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    private Optional<Folder> getFolder(Usuario usuario, String nombre) {
        return folderRepository
                .findByNombreAndPropietario(nombre, usuario);
    }

    public ResponseEntity<String> validarFolder(Long mensajeId, Usuario usuario) throws MensajeNoExisteExcepcion {
        Mensaje mensajeRevisar = repositorioMensaje.findById(mensajeId).orElseThrow(() -> new MensajeNoExisteExcepcion("El mensaje no existe"));
        return mensajeRevisar.getFolder().stream().anyMatch(
                folder -> Arrays.asList(CarpetasDefecto.ENTRADA.getNombreCarpeta(), CarpetasDefecto.ENVIADOS.getNombreCarpeta())
                        .contains(folder.getNombre()) && folder.getPropietario().getId().equals(usuario.getId())) ?
                ResponseEntity.status(HttpStatus.OK).body("Pertenece a la Carpeta de Entrada/Enviados") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Pertenece a la Carpeta de Entrada/Enviados");
    }

    public ResponseEntity<String> eliminarMensajeFolder(MensajeAEliminarDeCarpeta mensajeAEliminarDeCarpeta, Usuario usuario)
            throws FolderNoExisteException, MensajePerteneceCarpetaOrigenExcepcion, MensajeNoExisteExcepcion, MensajePropietarioNoExisteExcepcion {
        // Verificar si la carpeta existe
        Folder folder = folderRepository.findByIdAndPropietario(mensajeAEliminarDeCarpeta.folderId(), usuario).orElseThrow(() -> new FolderNoExisteException("No existe la carpeta buscada"));

        // Verificar si la carpeta no es de entrada o enviados
        if (Arrays.asList(CarpetasDefecto.ENTRADA.getNombreCarpeta(), CarpetasDefecto.ENVIADOS.getNombreCarpeta()).contains(folder.getNombre())) {
            throw new MensajePerteneceCarpetaOrigenExcepcion("El mensaje pertenece a la carpeta Entrada o enviados");
        }

        // Verificar si el mensaje existe
        Mensaje mensaje = folder.getMensajes().stream().filter(
                mensajes -> mensajes.getId().equals(mensajeAEliminarDeCarpeta.mensajeId())
        ).findFirst().orElseThrow(() -> new MensajeNoExisteExcepcion("El mensaje con id " + mensajeAEliminarDeCarpeta.mensajeId() + " no existe"));

        // Verificar si el mensaje corresponde a un envio o recibido del usuario
        MensajePropietario MensajePropietario = repositorioMensajePropietario.findByUsuario(usuario).stream().filter(mensajeDestinatario -> mensajeDestinatario.getMensaje().getId().equals(mensajeAEliminarDeCarpeta.mensajeId())).findFirst().orElseThrow(() -> new MensajePropietarioNoExisteExcepcion("No Existe una relación entre el mensaje y un usuario"));
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
        repositorioMensaje.save(mensaje);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<String> eliminarMensaje(MensajeAEliminar mensajeAEliminar, Usuario usuario)
            throws MensajeNoExisteExcepcion {
        // Verificar si el mensaje existe
        Mensaje mensaje = repositorioMensaje.findById(mensajeAEliminar.mensajeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El mensaje no existe"));
        // Lista para agrupar las carpetas en caso de que un mensaje este la bandeja de entrada como la de salida
        Set<Folder> folders = new HashSet<>();
        mensaje.getFolder().forEach(folder -> {
            if (folder.getPropietario().getId().equals(usuario.getId())) {
                folders.add(folder);
            }
        });
        if (folders.stream().noneMatch(folder -> folder.getMensajes().contains(mensaje))) {
            throw new MensajeNoExisteExcepcion("El mensaje con id " + mensajeAEliminar.mensajeId() + " no pertenece al usuario");
        }
        // Desvincular el mensaje de la carpeta y viceversa
        folders.forEach(folder -> FolderService.desvincularMensajeFolder(mensaje, folder));
        repositorioMensaje.save(mensaje);
        // Eliminar el registro que relaciona un mensaje con sus destinatario
        repositorioMensajePropietario.delete(mensaje.getMensajeDestinatario().stream()
                .filter(mensajesDestinatario -> mensajesDestinatario.getMensaje().getId().equals(mensaje.getId()) && mensajesDestinatario.getUsuario().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow(() -> new MensajeNoExisteExcepcion("El mensaje no existe")));
        // Verificar si la carpeta actualmente pertenece a alguna carpeta de lo contrario será eliminado completamente de la base de datos
        if (mensaje.getFolder().isEmpty()) {
            repositorioMensaje.delete(mensaje);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
