package com.example.codemail.mensajepropietario;

import com.example.codemail.mensaje.MensajeNoExisteException;
import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensajes/complejos")
public class MensajePropietarioControlador {
    private final MensajePropietarioService mensajePropietarioService;

    public MensajePropietarioControlador(MensajePropietarioService mensajePropietarioService) {
        this.mensajePropietarioService = mensajePropietarioService;
    }

    @GetMapping("/obtener/{folder}")
    public ResponseEntity<List<MensajePropietarioEntrega>> obtenerMensajes(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer folder
    ) throws MensajeNoExisteException {
        return mensajePropietarioService.obtenerMensajes(usuario, folder);
    }

    @PostMapping("/revisar")
    public ResponseEntity<String> revisarMensaje(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody MensajePropietarioRevisar mensajePropietarioRevisar
    ) throws MensajePropietarioNoExisteException, MensajeNoExisteException {
        return mensajePropietarioService.revisarMensaje(usuario, mensajePropietarioRevisar);
    }
}
