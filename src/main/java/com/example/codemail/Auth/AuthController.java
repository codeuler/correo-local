package com.example.codemail.Auth;

import com.example.codemail.errores.ManejadorDeErroresHttp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController implements ManejadorDeErroresHttp {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/registro")
    public String registroTemplate() {
        return "registro/registro";
    }

    @GetMapping("/login")
    public String loginTemplate() {
        return "login/login";
    }

    @GetMapping("/correo")
    public String correoTemplate() {
        return "correo/correo";
    }

    @PostMapping(value = "login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {
        try {
            AuthResponse autenticacion = authService.login(request);
            return ResponseEntity.ok(autenticacion);
        } catch (AuthenticationException authenticationManager) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    authenticationManager.getMessage()
            );
        }
    }

    @PostMapping(value = "registro")
    public ResponseEntity<AuthResponse> registro(
            @RequestBody @Validated RegisterRequest request
    ) {
        /*
         * En caso de que el correo que se ingresa exista, se devolverá un código de error 409
         * de lo contrario se creará el usuario en la base de datos
         */
        if (authService.buscarUsuario(request.correo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok(authService.registro(request));
        }
    }

}
