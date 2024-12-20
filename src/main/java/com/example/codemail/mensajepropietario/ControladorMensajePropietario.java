package com.example.codemail.mensajepropietario;

import com.example.codemail.mensaje.MensajeNoExisteExcepcion;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensajes-propietarios")
public class ControladorMensajePropietario {
    private final ServicioMensajePropietario servicioMensajePropietario;

    public ControladorMensajePropietario(ServicioMensajePropietario servicioMensajePropietario) {
        this.servicioMensajePropietario = servicioMensajePropietario;
    }

    @GetMapping("/carpetas/{idCarpeta}")
    public ResponseEntity<List<MensajePropietarioRespuesta>> obtenerMensajes(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer idCarpeta
    ) throws MensajeNoExisteExcepcion {
        return servicioMensajePropietario.obtenerMensajes(usuario, idCarpeta);
    }

    @PutMapping("/mensajes/{idMensaje}/estado")
    public ResponseEntity<String> revisarMensaje(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idMensaje
    ) throws MensajePropietarioNoExisteExcepcion, MensajeNoExisteExcepcion {
        return servicioMensajePropietario.revisarMensaje(usuario, idMensaje);
    }
}
