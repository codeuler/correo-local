package com.example.codemail.mensaje;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.folder.CarpetasDefecto;
import com.example.codemail.folder.Folder;
import com.example.codemail.folder.FolderRepository;
import com.example.codemail.folder.FolderService;
import com.example.codemail.mensajepropietario.MensajePropietario;
import com.example.codemail.mensajepropietario.MensajePropietarioRepository;
import com.example.codemail.mensajepropietario.MensajePropietarioService;
import com.example.codemail.mensajepropietario.RolMensajePropietario;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import com.example.codemail.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MensajeService extends UsuarioService implements RequestTokenExtractor {
    private final FolderRepository folderRepository;
    private final MensajeRepository mensajeRepository;
    private final MensajeMapper mensajeMapper;
    private final MensajePropietarioService mensajePropietarioService;
    private final MensajePropietarioRepository mensajePropietarioRepository;

    public MensajeService(JwtService jwtService, UsuarioRepository usuarioRepository, FolderRepository folderRepository,
                          MensajeRepository mensajeRepository, MensajeMapper mensajeMapper,
                          MensajePropietarioService mensajePropietarioService,
                          MensajePropietarioRepository mensajePropietarioRepository) {
        super(jwtService, usuarioRepository);
        this.folderRepository = folderRepository;
        this.mensajeRepository = mensajeRepository;
        this.mensajeMapper = mensajeMapper;
        this.mensajePropietarioService = mensajePropietarioService;
        this.mensajePropietarioRepository = mensajePropietarioRepository;
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

    public ResponseEntity<?> cambiarFolder(MensajeCambiar mensajeCambiar) {
        Optional<Mensaje> mensajeOptional = mensajeRepository.findById(mensajeCambiar.idMesajeCambiar());
        Optional<Folder> folderOptional = folderRepository.findById(mensajeCambiar.idFolderOrigen());
        Optional<Folder> folderOptionalDeCambio = folderRepository.findById(mensajeCambiar.idFolderDestino());
        if (!(mensajeOptional.isPresent() && folderOptional.isPresent() && folderOptionalDeCambio.isPresent())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Mensaje mensaje = mensajeOptional.get();
        Folder folderBase = folderOptional.get();
        Folder folderCambio = folderOptionalDeCambio.get();
        if(folderCambio.getNombre().equals(CarpetasDefecto.ENTRADA.getNombreCarpeta()) || folderCambio.getNombre().equals(CarpetasDefecto.ENVIADOS.getNombreCarpeta()) || folderCambio.getId().equals(folderBase.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede cambiar a folder de Enviados, Entrada o sí mismo");
        }
        Usuario usuario = folderBase.getPropietario();
        if(mensajePropietarioRepository.findByUsuarioAndMensaje(usuario,mensaje).orElseThrow().getRolMensajePropietario().equals(RolMensajePropietario.AMBOS) && (folderBase.getNombre().equals(CarpetasDefecto.ENTRADA.getNombreCarpeta()) || folderBase.getNombre().equals(CarpetasDefecto.ENVIADOS.getNombreCarpeta()))) {
            // Tengo que encontrar la bandeja de entrada y enviados del usuario, desvicular el mensaje de los folders y viceversa
            usuario.getFolders().stream()
                    .filter(folder -> folder.getNombre().equals(CarpetasDefecto.ENTRADA.getNombreCarpeta()) || folder.getNombre().equals(CarpetasDefecto.ENVIADOS.getNombreCarpeta()))
                    .forEach(folder -> FolderService.desvincularMensajeFolder(mensaje,folder));
        } else {
            // Se desvincula del folder anterior el mensaje
            // Se desvincula del mensaje el folder anterior
            FolderService.desvincularMensajeFolder(mensaje,folderBase);
        }
        // Se vincula al folder nuevo el mensaje
        // Se vincula al mensaje el nuevo folder
        FolderService.vincularMensajeFolder(mensaje,folderCambio);

        mensajeRepository.save(mensaje);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    private Optional<Folder> getFolder(Usuario usuario,String nombre) {
        return folderRepository
                .findByNombreAndPropietario(nombre, usuario);
    }


    public ResponseEntity<?> validarFolder(Integer mensajeId, HttpServletRequest request) {
        Usuario usuario = getUsuario(request);
        return mensajeRepository.findByIdAndAndUsuario(mensajeId,usuario).map(mensaje -> (mensaje.getFolder()
                .stream()
                .anyMatch(folder -> Arrays.asList(CarpetasDefecto.ENTRADA.getNombreCarpeta(), CarpetasDefecto.ENVIADOS.getNombreCarpeta()).contains(folder.getNombre()) && folder.getPropietario().getId().equals(usuario.getId()))) ?
                ResponseEntity.ok().build() : ResponseEntity.notFound().build()).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<?> eliminarMensajeFolder(MensajeEliminarFolder mensajeEliminarFolder, HttpServletRequest request) {
        // Buscar el usuario
        Usuario usuario = getUsuario(request);

        // Verificar si la carpeta existe
        Optional<Folder> optionalFolder = folderRepository.findByIdAndPropietario(mensajeEliminarFolder.folderId(),usuario);
        if(optionalFolder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Folder folder = optionalFolder.get();

        // Verificar si la carpeta no es de entrada o enviados
        if(Arrays.asList(CarpetasDefecto.ENTRADA.getNombreCarpeta(),CarpetasDefecto.ENVIADOS.getNombreCarpeta()).contains(folder.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El mensaje pertenece a la carpeta Entrada o enviados");
        }

        // Verificar si el mensaje existe
        Optional<Mensaje> optionalMensaje = mensajeRepository.findById(mensajeEliminarFolder.mensajeId());
        if(optionalMensaje.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Mensaje mensaje = optionalMensaje.get();

        // Verificar si el mensaje corresponde a un envio o recibido del usuario
        Optional<MensajePropietario> optionalMensajePropietario = usuario.getMensajesDestinatario().stream().filter(mensajeDestinatario -> mensajeDestinatario.getMensaje().getId().equals(mensajeEliminarFolder.mensajeId())).findFirst();
        Optional<Folder> optionalFolderEntrada = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENTRADA.getNombreCarpeta(), usuario);
        Optional<Folder> optinalFolderEnviados = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENVIADOS.getNombreCarpeta(), usuario);
        if(optionalMensajePropietario.isEmpty() || optionalFolderEntrada.isEmpty() || optinalFolderEnviados.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Folder folderEntrada = optionalFolderEntrada.get();
        Folder folderEnviados = optinalFolderEnviados.get();
        FolderService.desvincularMensajeFolder(mensaje,folder);

        // Devolver el mensaje a su carpeta de origen
        switch (optionalMensajePropietario.get().getRolMensajePropietario()) {
            case REMITENTE -> FolderService.vincularMensajeFolder(mensaje,folderEnviados);
            case DESTINATARIO -> FolderService.vincularMensajeFolder(mensaje,folderEntrada);
            case AMBOS -> {
                FolderService.vincularMensajeFolder(mensaje,folderEnviados);
                FolderService.vincularMensajeFolder(mensaje,folderEntrada);
            }
            default -> {
            }
        }
        mensajeRepository.save(mensaje);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<?> eliminarMensaje(MensajeEliminar mensajeEliminar, HttpServletRequest request) {
        // Buscar el usuario
        Usuario usuario = getUsuario(request);
        // Verificar si el mensaje existe
        Mensaje mensaje = mensajeRepository.findById(mensajeEliminar.mensajeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"El mensaje no existe"));
        // Lista para agrupar las carpetas en caso de que un mensaje este la bandeja de entrada como la de salida
        Set<Folder> folders = new HashSet<>();
        mensaje.getFolder().forEach(folder -> {
            if (folder.getPropietario().getId().equals(usuario.getId())){
                folders.add(folder);
            }
        });
        if (folders.stream().noneMatch(folder -> folder.getMensajes().contains(mensaje))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El mensaje no existe dentro de ninguna carpeta del usuario");
        }
        // Desvincular el mensaje de la carpeta y viceversa
        folders.forEach(folder -> FolderService.desvincularMensajeFolder(mensaje,folder));
        mensajeRepository.save(mensaje);
        // Verificar si la carpeta actualmente pertenece a alguna carpeta de lo contrario será eliminado completamente de la base de datos
        if (mensaje.getFolder().isEmpty()) {
            mensajeRepository.delete(mensaje);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
