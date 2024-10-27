package com.example.codemail.folder;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.errores.ManejadorDeErroresHttp;
import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensajepropietario.MensajePropietarioRepository;
import com.example.codemail.mensajepropietario.RolMensajePropietario;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FolderService implements RequestTokenExtractor, ManejadorDeErroresHttp {
    private final FolderRepository folderRepository;
    private final FolderMapper folderMapper;
    private final MensajePropietarioRepository mensajePropietarioRepository;

    public FolderService(FolderRepository folderRepository, JwtService jwtService, UsuarioRepository usuarioRepository, FolderMapper folderMapper, MensajePropietarioRepository mensajePropietarioRepository) {
        this.folderRepository = folderRepository;
        this.folderMapper = folderMapper;
        this.mensajePropietarioRepository = mensajePropietarioRepository;
    }

    public ResponseEntity<?> getAll(Usuario usuario) {
        Set<FolderRespuesta> folders = usuario.getFolders()
                .stream()
                .map(folderMapper::toFolderRespuesta)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(folders);
    }

    public ResponseEntity<?> crearFolder(Usuario usuario, FolderGuardar folderGuardar) {
        if (buscarFolderRepetido(usuario,folderGuardar.nombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder ya existe");
        } else {
            folderRepository.save(folderMapper.toFolder(usuario, folderGuardar.nombre()));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    private boolean buscarFolderRepetido(Usuario usuario, String nombreFolder) {
        Optional<Folder> carpeta = usuario.getFolders()
                .stream()
                .filter(
                        folder -> folder.getNombre().equals(nombreFolder)
                ).findFirst();
        return carpeta.isPresent();
    }

    public void crearFolder(Usuario usuario, String nombreFolder) {
        folderRepository.save(folderMapper.toFolder(usuario, nombreFolder));
    }

    public ResponseEntity<?> actualizarFolder(Usuario usuario, Integer idCarpeta, FolderGuardar folderGuardar) {
        Optional<Folder> optionalFolder = folderRepository.findById(idCarpeta);
        if (optionalFolder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Folder folder = optionalFolder.get();
        if (buscarFolderRepetido(usuario, folderGuardar.nombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder ya existe");
        }
        folder.setNombre(folderGuardar.nombre());
        folderRepository.save(folder);
        return ResponseEntity.ok().body(folderMapper.toFolderRespuesta(folder));
    }

    public ResponseEntity<?> eliminarFolder(Usuario usuario, FolderEliminar folderEliminar) {
        Optional<Folder> folderOptional = folderRepository.findByIdAndPropietario(folderEliminar.folderId(),usuario);
        if (folderOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Folder folder = folderOptional.get();
        Folder folderEntrada = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENTRADA.getNombreCarpeta(), usuario).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Folder folderEnviados = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENVIADOS.getNombreCarpeta(), usuario).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (folder.getId().equals(folderEntrada.getId()) || folder.getId().equals(folderEnviados.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Los folder de Entrada y enviados no se puede eliminar");
        }
        //Retornar los mensajes a la carpeta a la cual perteneció inicialmente (Entrada o Enviados)
        Set<Mensaje> folderCopia = Set.copyOf(folder.getMensajes());
        folderCopia.forEach(
                mensaje -> {
                    desvincularMensajeFolder(mensaje,folder);
                    // En caso de que sea un auto envio, el mensaje se manda para ambas bandejas
                    if(mensajePropietarioRepository.findByUsuarioAndMensaje(usuario,mensaje).orElseThrow().getRolMensajePropietario().equals(RolMensajePropietario.AMBOS)){
                        vincularMensajeFolder(mensaje,folderEntrada);
                        vincularMensajeFolder(mensaje,folderEnviados);
                    } else {
                        // Si el usuario es el remitente entonces se manda el mensaje a la bandeja de enviados, de lo contrario a la badeja de entrada
                        vincularMensajeFolder(
                                mensaje,
                                (mensajePropietarioRepository.findByUsuarioAndMensaje(usuario,mensaje)
                                        .orElseThrow()
                                        .getRolMensajePropietario()
                                        .equals(RolMensajePropietario.REMITENTE)
                                )?folderEnviados:folderEntrada);
                    }
                }
        );
        folderRepository.saveAll(Arrays.asList(folderEntrada,folderEnviados,folder));
        folderRepository.delete(folder);
        return ResponseEntity.ok().body(folderMapper.toFolderRespuesta(folder));
    }
    public static void desvincularMensajeFolder(Mensaje mensaje, Folder folder) {
        folder.getMensajes().remove(mensaje);
        mensaje.getFolder().remove(folder);
    }

    public static void vincularMensajeFolder(Mensaje mensaje, Folder folder) {
        folder.getMensajes().add(mensaje);
        mensaje.getFolder().add(folder);
    }
}
