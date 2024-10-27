package com.example.codemail.mensajepropietario;

import com.example.codemail.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mensajes/complejos")
public class MensajePropietarioControlador {
    private final MensajePropietarioService mensajePropietarioService;

    public MensajePropietarioControlador(MensajePropietarioService mensajePropietarioService) {
        this.mensajePropietarioService = mensajePropietarioService;
    }

    @GetMapping("/obtener/{folder}")
    public ResponseEntity<?> obtenerMensajes(@AuthenticationPrincipal Usuario usuario, @PathVariable Integer folder) {
        return mensajePropietarioService.obtenerMensajes(usuario,folder);
    }
    @PostMapping("/revisar")
    public ResponseEntity<?> revisarMensaje(@AuthenticationPrincipal Usuario usuario, @RequestBody MensajePropietarioRevisar mensajePropietarioRevisar) {
        return mensajePropietarioService.revisarMensaje(usuario, mensajePropietarioRevisar);
    }
}
