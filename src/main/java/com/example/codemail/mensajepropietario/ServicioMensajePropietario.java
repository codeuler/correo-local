package com.example.codemail.mensajepropietario;

import com.example.codemail.carpeta.Carpeta;
import com.example.codemail.carpeta.RepositorioCarpeta;
import com.example.codemail.mensaje.Mensaje;
import com.example.codemail.mensaje.MensajeNoExisteExcepcion;
import com.example.codemail.mensaje.RepositorioMensaje;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioMensajePropietario {
    private final RepositorioMensajePropietario repositorioMensajePropietario;
    private final MensajePropietarioMapeador mensajePropietarioMapeador;
    private final RepositorioMensaje repositorioMensaje;
    private final RepositorioCarpeta repositorioCarpeta;

    public ServicioMensajePropietario(RepositorioMensajePropietario repositorioMensajePropietario, MensajePropietarioMapeador mensajePropietarioMapeador, RepositorioMensaje repositorioMensaje, RepositorioCarpeta repositorioCarpeta) {
        this.repositorioMensajePropietario = repositorioMensajePropietario;
        this.mensajePropietarioMapeador = mensajePropietarioMapeador;
        this.repositorioMensaje = repositorioMensaje;
        this.repositorioCarpeta = repositorioCarpeta;
    }

    public void guardarMensajePropietario(MensajePropietario mensajePropietario) {
        repositorioMensajePropietario.save(mensajePropietario);
    }

    public ResponseEntity<List<MensajePropietarioRespuesta>> obtenerMensajes(Usuario usuario, Integer folderId) throws MensajeNoExisteExcepcion {
        //Obtener el folder especifico del usuario
        Carpeta carpeta = repositorioCarpeta.findByIdAndPropietario(folderId,usuario)
                .orElseThrow(() -> new MensajeNoExisteExcepcion("El folder con id" + folderId + " no existe"));

        return ResponseEntity.ok(carpeta.getMensajes()
                .stream()
                .map(mensaje -> repositorioMensajePropietario.findByUsuarioAndMensaje(usuario, mensaje))
                .flatMap(Optional::stream)
                .map(mensajePropietarioMapeador::toMensajePropietarioEntrega)
                .sorted((mensajePropitarioA, mensajePropitarioB) -> mensajePropitarioA.fechaEnvio().compareTo(mensajePropitarioB.fechaEnvio()) * -1)
                .collect(Collectors.toList())
        );
    }

    public ResponseEntity<String> revisarMensaje(Usuario usuario, MensajePropietarioARevisar mensajePropietarioARevisar) throws MensajeNoExisteExcepcion, MensajePropietarioNoExisteExcepcion {
        // Revisar que el mensaje exista
        Mensaje mensaje = repositorioMensaje.findById(mensajePropietarioARevisar.mensajeId()).orElseThrow(() -> new MensajeNoExisteExcepcion("No existe el mensaje con id " + mensajePropietarioARevisar.mensajeId()));
        // Revisar que el mensaje tenga relación con algún destinatario
        MensajePropietario mensajePropietarioEntregar = repositorioMensajePropietario.findByUsuarioAndMensaje(usuario, mensaje).orElseThrow(() -> new MensajePropietarioNoExisteExcepcion("No se ha encontrado dicho mensaje asociado al usuario"));
        mensajePropietarioEntregar.setRevisado(true);
        repositorioMensajePropietario.save(mensajePropietarioEntregar);
        return ResponseEntity.ok("Mensaje revisado con exito");
    }
}
