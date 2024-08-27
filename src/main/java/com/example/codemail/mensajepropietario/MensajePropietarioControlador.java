package com.example.codemail.mensajepropietario;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mensajes/complejos")
public class MensajePropietarioControlador {
    private final MensajePropietarioService mensajePropietarioService;

    public MensajePropietarioControlador(MensajePropietarioService mensajePropietarioService) {
        this.mensajePropietarioService = mensajePropietarioService;
    }

    @GetMapping("/obtener/{folder}")
    public ResponseEntity<?> obtenerMensajes(HttpServletRequest request, @PathVariable String folder) {
        return mensajePropietarioService.obtenerMensajes(request,folder);
    }
}
