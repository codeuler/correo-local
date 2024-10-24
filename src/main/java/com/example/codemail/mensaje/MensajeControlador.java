package com.example.codemail.mensaje;

import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.errores.ManejadorDeErroresHttp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mensajes")
public class MensajeControlador implements RequestTokenExtractor, ManejadorDeErroresHttp {
    private final MensajeService mensajeService;

    public MensajeControlador(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> enviarMensaje(
            @RequestBody MensajeEnviado mensajeEnviado,
            HttpServletRequest request
    ) {
        return mensajeService.enviarMensaje(mensajeEnviado, request);
    }
    @PutMapping("/cambiarFolder")
    public ResponseEntity<?> cambiarFolder(
            @RequestBody MensajeCambiar mensajeCambiar
    ) {
        return mensajeService.cambiarFolder(mensajeCambiar);
    }
    @GetMapping("/{mensajeId}/validacionFolder")
    public ResponseEntity<?> validacionFolder(
            @PathVariable Integer mensajeId,
            HttpServletRequest request
    ) {
        return mensajeService.validarFolder(mensajeId,request);
    }

    @DeleteMapping("/eliminar/folder")
    public ResponseEntity<?> eliminarFolder(@RequestBody MensajeEliminarFolder mensajeEliminarFolder, HttpServletRequest request) {
        return mensajeService.eliminarMensajeFolder(mensajeEliminarFolder, request);
    }
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarMensaje(@RequestBody MensajeEliminar mensajeEliminar, HttpServletRequest request) {
        return mensajeService.eliminarMensaje(mensajeEliminar, request);
    }
}
