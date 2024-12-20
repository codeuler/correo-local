package com.example.codemail.mensaje;

import com.example.codemail.carpeta.*;
import com.example.codemail.mensajepropietario.*;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.CorreoNoValidoExcepcion;
import com.example.codemail.usuario.RepositorioUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServicioMensaje {
    private final RepositorioCarpeta repositorioCarpeta;
    private final RepositorioMensaje repositorioMensaje;
    private final MensajeMapeador mensajeMapeador;
    private final ServicioMensajePropietario servicioMensajePropietario;
    private final RepositorioMensajePropietario repositorioMensajePropietario;
    private final RepositorioUsuario repositorioUsuario;

    public ServicioMensaje(RepositorioUsuario repositorioUsuario, RepositorioCarpeta repositorioCarpeta,
                           RepositorioMensaje repositorioMensaje, MensajeMapeador mensajeMapeador,
                           ServicioMensajePropietario servicioMensajePropietario,
                           RepositorioMensajePropietario repositorioMensajePropietario) {
        this.repositorioCarpeta = repositorioCarpeta;
        this.repositorioMensaje = repositorioMensaje;
        this.mensajeMapeador = mensajeMapeador;
        this.servicioMensajePropietario = servicioMensajePropietario;
        this.repositorioMensajePropietario = repositorioMensajePropietario;
        this.repositorioUsuario = repositorioUsuario;
    }

    public ResponseEntity<String> enviarMensaje(MensajeEnviado mensajeEnviado, Usuario usuario)
            throws CarpetaNoExisteExcepcion, CorreoNoValidoExcepcion {
        // Encontrar todos los usuarios que tengan por id el correo que se ha enviado en MensajeEnviado
        Set<Usuario> destinatarios = mensajeEnviado
                .correoDestinatarios()
                .stream()
                .map(repositorioUsuario::findByEmail)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        // Buscar todos los folder de entrada de los detinarios
        Set<Carpeta> carpetaEntrada = destinatarios
                .stream()
                .map(user -> getFolder(user, CarpetaPorDefecto.ENTRADA.getNombreCarpeta()))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        // Hallar el folder Envio del usuario
        Carpeta folder = getFolder(usuario, CarpetaPorDefecto.ENVIADOS.getNombreCarpeta()).orElseThrow(() -> new CarpetaNoExisteExcepcion("No Existe la carpeta de 'Enviados'"));
        //En caso de que no exista ningún correo de destinatario
        if (destinatarios.isEmpty()) {
            throw new CorreoNoValidoExcepcion("La o las direcciones de correo que se ingresaron no son válidas");
        }
        Mensaje mensaje = mensajeMapeador.toMensaje(mensajeEnviado, usuario, carpetaEntrada);
        // Agregar el mensaje a cada folder
        carpetaEntrada.forEach(carpeta -> carpeta.getMensajes().add(mensaje));
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

    public ResponseEntity<String> cambiarFolder(MensajeAActualizar mensajeAActualizar, Long idMensajeCambiar, Usuario usuario)
            throws MensajeNoExisteExcepcion, ErrorCambioCarpetaExcepcion {
        Carpeta carpetaBase = repositorioCarpeta.findById(mensajeAActualizar.idFolderOrigen()).orElseThrow(() -> new MensajeNoExisteExcepcion("El folder " + mensajeAActualizar.idFolderOrigen() + " no existe"));
        Carpeta carpetaCambio = repositorioCarpeta.findById(mensajeAActualizar.idFolderDestino()).orElseThrow(() -> new MensajeNoExisteExcepcion("El folder " + mensajeAActualizar.idFolderDestino() + " no existe"));
        Mensaje mensaje = repositorioMensaje.findByIdAndCarpeta(idMensajeCambiar, Set.of(carpetaBase)).orElseThrow(() -> new MensajeNoExisteExcepcion("El mensaje no existe"));
        if (carpetaCambio.getNombre().equals(CarpetaPorDefecto.ENTRADA.getNombreCarpeta()) || carpetaCambio.getNombre().equals(CarpetaPorDefecto.ENVIADOS.getNombreCarpeta()) || carpetaCambio.getId().equals(carpetaBase.getId())) {
            throw new ErrorCambioCarpetaExcepcion("No se puede cambiar a carpeta: Entrada, Enviados o sí mismo");
        }
        if (repositorioMensajePropietario.findByUsuarioAndMensaje(usuario, mensaje).orElseThrow().getRolMensajePropietario().equals(RolMensajePropietario.AMBOS) && (carpetaBase.getNombre().equals(CarpetaPorDefecto.ENTRADA.getNombreCarpeta()) || carpetaBase.getNombre().equals(CarpetaPorDefecto.ENVIADOS.getNombreCarpeta()))) {
            // Se encuentra la bandeja de entrada y enviados del usuario, desvicular el mensaje de los folders y viceversa
            repositorioCarpeta.findAllByPropietario(usuario).stream()
                    .filter(folder -> folder.getNombre().equals(CarpetaPorDefecto.ENTRADA.getNombreCarpeta()) || folder.getNombre().equals(CarpetaPorDefecto.ENVIADOS.getNombreCarpeta()))
                    .forEach(folder -> ServicioCarpeta.desvincularMensajeFolder(mensaje, folder));
        } else {
            // Se desvincula del folder anterior el mensaje
            // Se desvincula del mensaje el folder anterior
            ServicioCarpeta.desvincularMensajeFolder(mensaje, carpetaBase);
        }
        // Se vincula al folder nuevo el mensaje
        // Se vincula al mensaje el nuevo folder
        ServicioCarpeta.vincularMensajeFolder(mensaje, carpetaCambio);

        repositorioMensaje.save(mensaje);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    private Optional<Carpeta> getFolder(Usuario usuario, String nombre) {
        return repositorioCarpeta
                .findByNombreAndPropietario(nombre, usuario);
    }

    public ResponseEntity<String> validarFolder(Long mensajeId, Usuario usuario) throws MensajeNoExisteExcepcion {
        Mensaje mensajeRevisar = repositorioMensaje.findById(mensajeId).orElseThrow(() -> new MensajeNoExisteExcepcion("El mensaje no existe"));
        return mensajeRevisar.getFolder().stream().anyMatch(
                folder -> Arrays.asList(CarpetaPorDefecto.ENTRADA.getNombreCarpeta(), CarpetaPorDefecto.ENVIADOS.getNombreCarpeta())
                        .contains(folder.getNombre()) && folder.getPropietario().getId().equals(usuario.getId())) ?
                ResponseEntity.status(HttpStatus.OK).body("Pertenece a la Carpeta de Entrada/Enviados") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Pertenece a la Carpeta de Entrada/Enviados");
    }

    public ResponseEntity<String> eliminarMensaje(Long mensajeId, Usuario usuario)
            throws MensajeNoExisteExcepcion {
        // Verificar si el mensaje existe
        Mensaje mensaje = repositorioMensaje.findById(mensajeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El mensaje no existe"));
        // Lista para agrupar las carpetas en caso de que un mensaje este la bandeja de entrada como la de salida
        Set<Carpeta> carpetas = new HashSet<>();
        mensaje.getFolder().forEach(folder -> {
            if (folder.getPropietario().getId().equals(usuario.getId())) {
                carpetas.add(folder);
            }
        });
        if (carpetas.stream().noneMatch(folder -> folder.getMensajes().contains(mensaje))) {
            throw new MensajeNoExisteExcepcion("El mensaje con id " + mensajeId + " no pertenece al usuario");
        }
        // Desvincular el mensaje de la carpeta y viceversa
        carpetas.forEach(folder -> ServicioCarpeta.desvincularMensajeFolder(mensaje, folder));
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
