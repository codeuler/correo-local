package com.example.codemail.mensajepropietario;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mensajes/complejos")
public class MensajePropietarioControlador {
    private final MensajePropietarioService mensajePropietarioService;

    public MensajePropietarioControlador(MensajePropietarioService mensajePropietarioService) {
        this.mensajePropietarioService = mensajePropietarioService;
    }

    @GetMapping("/obtener/{folder}")
    public ResponseEntity<?> obtenerMensajes(HttpServletRequest request, @PathVariable Integer folder) {
        return mensajePropietarioService.obtenerMensajes(request,folder);
    }
    @PostMapping("/revisar")
    public ResponseEntity<?> revisarMensaje(HttpServletRequest request,@RequestBody MensajePropietarioRevisar mensajePropietarioRevisar) {
        return mensajePropietarioService.revisarMensaje(request, mensajePropietarioRevisar);
    }
}
