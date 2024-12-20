package com.example.codemail.carpeta;

import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensajepropietario.MensajePropietarioNoExisteExcepcion;
import com.example.codemail.mensajepropietario.MensajePropietario;
import com.example.codemail.mensajepropietario.RepositorioMensajePropietario;
import com.example.codemail.mensajepropietario.RolMensajePropietario;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServicioCarpeta {
    private final RepositorioCarpeta repositorioCarpeta;
    private final CarpetaMapeador carpetaMapeador;
    private final RepositorioMensajePropietario repositorioMensajePropietario;

    public ServicioCarpeta(RepositorioCarpeta repositorioCarpeta, CarpetaMapeador carpetaMapeador, RepositorioMensajePropietario repositorioMensajePropietario) {
        this.repositorioCarpeta = repositorioCarpeta;
        this.carpetaMapeador = carpetaMapeador;
        this.repositorioMensajePropietario = repositorioMensajePropietario;
    }

    public ResponseEntity<List<CarpetaRespuesta>> getAll(Usuario usuario) {
        List<CarpetaRespuesta> folders = repositorioCarpeta.findAllByPropietario(usuario)
                .stream()
                .map(carpetaMapeador::toFolderRespuesta)
                .sorted(
                        (folder1, folder2) -> folder1.nombre().compareToIgnoreCase(folder2.nombre())
                ).collect(Collectors.toList());
        return ResponseEntity.ok(folders);
    }

    public ResponseEntity<String> crearFolder(Usuario usuario, CarpetaAGuardar carpetaAGuardar) throws FolderYaExisteException {
        if (buscarFolderRepetido(usuario, carpetaAGuardar.nombre())) {
            throw new FolderYaExisteException("El nombre " + carpetaAGuardar.nombre() + " ya está siendo usado para otra carpeta");
        } else {
            repositorioCarpeta.save(carpetaMapeador.toFolder(usuario, carpetaAGuardar.nombre()));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    private boolean buscarFolderRepetido(Usuario usuario, String nombreFolder) {

        return repositorioCarpeta.findAllByPropietario(usuario)
                .stream()
                .anyMatch(folder -> folder.getNombre().equals(nombreFolder));
    }

    public void crearFolder(Usuario usuario, String nombreFolder) {
        repositorioCarpeta.save(carpetaMapeador.toFolder(usuario, nombreFolder));
    }

    public ResponseEntity<CarpetaRespuesta> actualizarFolder(
            Usuario usuario, Integer idCarpeta, CarpetaAGuardar carpetaAGuardar
    ) throws CarpetaNoExisteExcepcion, FolderYaExisteException {
        Carpeta carpeta = repositorioCarpeta.findById(idCarpeta).orElseThrow(() -> new CarpetaNoExisteExcepcion("La carpeta solicitada no existe dentro de las carpetas almacenadas"));
        if (buscarFolderRepetido(usuario, carpetaAGuardar.nombre())) {
            throw new FolderYaExisteException("El nombre " + carpetaAGuardar.nombre() + " ya está siendo usado para otra carpeta");
        }
        carpeta.setNombre(carpetaAGuardar.nombre());
        repositorioCarpeta.save(carpeta);
        return ResponseEntity.ok().body(carpetaMapeador.toFolderRespuesta(carpeta));
    }

    public ResponseEntity<CarpetaRespuesta> eliminarFolder(Usuario usuario, Integer folderId) throws CarpetaNoExisteExcepcion, CarpetaImposibleEliminarExcepcion, MensajePropietarioNoExisteExcepcion {
        Carpeta carpeta = repositorioCarpeta.findByIdAndPropietario(folderId, usuario).orElseThrow(() -> new CarpetaNoExisteExcepcion("La carpeta solicitada no existe dentro de las carpetas almacenadas"));
        Carpeta carpetaEntrada = repositorioCarpeta.findByNombreAndPropietario(CarpetaPorDefecto.ENTRADA.getNombreCarpeta(), usuario).orElseThrow(() -> new CarpetaNoExisteExcepcion("La carpeta 'Entrada' no existe dentro de las carpetas almacenadas"));
        Carpeta carpetaEnviados = repositorioCarpeta.findByNombreAndPropietario(CarpetaPorDefecto.ENVIADOS.getNombreCarpeta(), usuario).orElseThrow(() -> new CarpetaNoExisteExcepcion("La carpeta 'Enviados' solicitada no existe dentro de las carpetas almacenadas"));
        // Verificar que el folder a eliminar no sea el de Entrada o Enviados.
        if (carpeta.getId().equals(carpetaEntrada.getId()) || carpeta.getId().equals(carpetaEnviados.getId())) {
            throw new CarpetaImposibleEliminarExcepcion("Los folder de Entrada y enviados no se puede eliminar");
        }
        //Retornar los mensajes a la carpeta a la cual perteneció inicialmente (Entrada o Enviados)
        Set<Mensaje> folderCopia = Set.copyOf(carpeta.getMensajes());
        try {
            folderCopia.forEach(
                    mensaje -> {
                        desvincularMensajeFolder(mensaje, carpeta);
                        // En caso de que sea un auto envio, el mensaje se manda para ambas bandejas
                        MensajePropietario mensajePropietario = repositorioMensajePropietario.findByUsuarioAndMensaje(usuario, mensaje).orElseThrow();
                        if (mensajePropietario.getRolMensajePropietario().equals(RolMensajePropietario.AMBOS)) {
                            vincularMensajeFolder(mensaje, carpetaEntrada);
                            vincularMensajeFolder(mensaje, carpetaEnviados);
                        } else {
                            // Si el usuario es el remitente entonces se manda el mensaje a la bandeja de enviados, de lo contrario a la badeja de entrada
                            vincularMensajeFolder(mensaje, (mensajePropietario.getRolMensajePropietario().equals(RolMensajePropietario.REMITENTE)) ? carpetaEnviados : carpetaEntrada);
                        }
                    }
            );
        } catch (Exception e) {
            throw new MensajePropietarioNoExisteExcepcion("No existe relación alguna entre el mensaje y algún destinatario");
        }
        repositorioCarpeta.saveAll(Arrays.asList(carpetaEntrada, carpetaEnviados, carpeta));
        repositorioCarpeta.delete(carpeta);
        return ResponseEntity.ok().body(carpetaMapeador.toFolderRespuesta(carpeta));
    }

    public static void desvincularMensajeFolder(Mensaje mensaje, Carpeta carpeta) {
        carpeta.getMensajes().remove(mensaje);
        mensaje.getFolder().remove(carpeta);
    }

    public static void vincularMensajeFolder(Mensaje mensaje, Carpeta carpeta) {
        carpeta.getMensajes().add(mensaje);
        mensaje.getFolder().add(carpeta);
    }

    public ResponseEntity<CarpetaRespuesta> buscarIdFolder(String nombreCarpeta, Usuario usuario) throws CarpetaNoExisteExcepcion {
        return ResponseEntity.ok(carpetaMapeador.toFolderRespuesta(repositorioCarpeta.findByNombreAndPropietario(nombreCarpeta, usuario)
                .orElseThrow(() -> new CarpetaNoExisteExcepcion("El folder con nombre: " + nombreCarpeta + " no existe"))));
    }
}
