package com.example.codemail.autenticacion;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class ControladorAutenticacion {
    public final ServicioAutenticacion servicioAutenticacion;

    public ControladorAutenticacion(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @GetMapping("/registro")
    public String registroTemplate() {
        return "registro/registro";
    }

    @GetMapping("/login")
    public String loginTemplate() {
        return "login/login";
    }

    @PostMapping(value = "login")
    public ResponseEntity<RespuestaAutenticacion> login(
            @RequestBody PeticionLogin request
    ) throws AutenticacionNoValidaExcepcion {
        return servicioAutenticacion.tryLogin(request);
    }

    @PostMapping(value = "registro")
    public ResponseEntity<RespuestaAutenticacion> registro(
            @RequestBody @Validated PeticionRegistro request
    ) throws ErrorRegistroExcepcion {
        return servicioAutenticacion.tryRegistro(request);
    }

}
