package com.example.codemail.folder;

import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteException;
import com.example.codemail.mensajepropietario.MensajePropietario;
import com.example.codemail.mensajepropietario.MensajePropietarioRepository;
import com.example.codemail.mensajepropietario.RolMensajePropietario;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final FolderMapper folderMapper;
    private final MensajePropietarioRepository mensajePropietarioRepository;

    public FolderService(FolderRepository folderRepository, FolderMapper folderMapper, MensajePropietarioRepository mensajePropietarioRepository) {
        this.folderRepository = folderRepository;
        this.folderMapper = folderMapper;
        this.mensajePropietarioRepository = mensajePropietarioRepository;
    }

    public ResponseEntity<List<FolderRespuesta>> getAll(Usuario usuario) {
        List<FolderRespuesta> folders = folderRepository.findAllByPropietario(usuario)
                .stream()
                .map(folderMapper::toFolderRespuesta)
                .sorted(
                        (folder1, folder2) -> folder1.nombre().compareToIgnoreCase(folder2.nombre())
                ).collect(Collectors.toList());
        return ResponseEntity.ok(folders);
    }

    public ResponseEntity<String> crearFolder(Usuario usuario, FolderGuardar folderGuardar) throws FolderYaExisteException {
        if (buscarFolderRepetido(usuario, folderGuardar.nombre())) {
            throw new FolderYaExisteException("El nombre " + folderGuardar.nombre() + " ya está siendo usado para otra carpeta");
        } else {
            folderRepository.save(folderMapper.toFolder(usuario, folderGuardar.nombre()));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    private boolean buscarFolderRepetido(Usuario usuario, String nombreFolder) {

        return folderRepository.findAllByPropietario(usuario)
                .stream()
                .anyMatch(folder -> folder.getNombre().equals(nombreFolder));
    }

    public void crearFolder(Usuario usuario, String nombreFolder) {
        folderRepository.save(folderMapper.toFolder(usuario, nombreFolder));
    }

    public ResponseEntity<FolderRespuesta> actualizarFolder(
            Usuario usuario, Integer idCarpeta, FolderGuardar folderGuardar
    ) throws FolderNoExisteException, FolderYaExisteException {
        Folder folder = folderRepository.findById(idCarpeta).orElseThrow(() -> new FolderNoExisteException("La carpeta solicitada no existe dentro de las carpetas almacenadas"));
        if (buscarFolderRepetido(usuario, folderGuardar.nombre())) {
            throw new FolderYaExisteException("El nombre " + folderGuardar.nombre() + " ya está siendo usado para otra carpeta");
        }
        folder.setNombre(folderGuardar.nombre());
        folderRepository.save(folder);
        return ResponseEntity.ok().body(folderMapper.toFolderRespuesta(folder));
    }

    public ResponseEntity<FolderRespuesta> eliminarFolder(Usuario usuario, FolderEliminar folderEliminar) throws FolderNoExisteException, FolderImposibleEliminarException, MensajePropietarioNoExisteException {
        Folder folder = folderRepository.findByIdAndPropietario(folderEliminar.folderId(), usuario).orElseThrow(() -> new FolderNoExisteException("La carpeta solicitada no existe dentro de las carpetas almacenadas"));
        Folder folderEntrada = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENTRADA.getNombreCarpeta(), usuario).orElseThrow(() -> new FolderNoExisteException("La carpeta 'Entrada' no existe dentro de las carpetas almacenadas"));
        Folder folderEnviados = folderRepository.findByNombreAndPropietario(CarpetasDefecto.ENVIADOS.getNombreCarpeta(), usuario).orElseThrow(() -> new FolderNoExisteException("La carpeta 'Enviados' solicitada no existe dentro de las carpetas almacenadas"));
        // Verificar que el folder a eliminar no sea el de Entrada o Enviados.
        if (folder.getId().equals(folderEntrada.getId()) || folder.getId().equals(folderEnviados.getId())) {
            throw new FolderImposibleEliminarException("Los folder de Entrada y enviados no se puede eliminar");
        }
        //Retornar los mensajes a la carpeta a la cual perteneció inicialmente (Entrada o Enviados)
        Set<Mensaje> folderCopia = Set.copyOf(folder.getMensajes());
        try {
            folderCopia.forEach(
                    mensaje -> {
                        desvincularMensajeFolder(mensaje, folder);
                        // En caso de que sea un auto envio, el mensaje se manda para ambas bandejas
                        MensajePropietario mensajePropietario = mensajePropietarioRepository.findByUsuarioAndMensaje(usuario, mensaje).orElseThrow();
                        if (mensajePropietario.getRolMensajePropietario().equals(RolMensajePropietario.AMBOS)) {
                            vincularMensajeFolder(mensaje, folderEntrada);
                            vincularMensajeFolder(mensaje, folderEnviados);
                        } else {
                            // Si el usuario es el remitente entonces se manda el mensaje a la bandeja de enviados, de lo contrario a la badeja de entrada
                            vincularMensajeFolder(mensaje, (mensajePropietario.getRolMensajePropietario().equals(RolMensajePropietario.REMITENTE)) ? folderEnviados : folderEntrada);
                        }
                    }
            );
        } catch (Exception e) {
            throw new MensajePropietarioNoExisteException("No existe relación alguna entre el mensaje y algún destinatario");
        }
        folderRepository.saveAll(Arrays.asList(folderEntrada, folderEnviados, folder));
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
