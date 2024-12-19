package com.example.codemail.mensajepropietario;

import com.example.codemail.mensaje.MensajeNoExisteExcepcion;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensajes/complejos")
public class ControladorMensajePropietario {
    private final ServicioMensajePropietario servicioMensajePropietario;

    public ControladorMensajePropietario(ServicioMensajePropietario servicioMensajePropietario) {
        this.servicioMensajePropietario = servicioMensajePropietario;
    }

    @GetMapping("/obtener/{folder}")
    public ResponseEntity<List<MensajePropietarioRespuesta>> obtenerMensajes(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer folder
    ) throws MensajeNoExisteExcepcion {
        return servicioMensajePropietario.obtenerMensajes(usuario, folder);
    }

    @PostMapping("/revisar")
    public ResponseEntity<String> revisarMensaje(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody MensajePropietarioARevisar mensajePropietarioARevisar
    ) throws MensajePropietarioNoExisteExcepcion, MensajeNoExisteExcepcion {
        return servicioMensajePropietario.revisarMensaje(usuario, mensajePropietarioARevisar);
    }
}
