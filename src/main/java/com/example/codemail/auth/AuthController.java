package com.example.codemail.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {
    public final AuthService authService;

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
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) throws AuthNoValidException {
        return authService.tryLogin(request);
    }

    @PostMapping(value = "registro")
    public ResponseEntity<AuthResponse> registro(
            @RequestBody @Validated RegisterRequest request
    ) throws AuthRegistrerException {
        return authService.tryRegistro(request);
    }

}
