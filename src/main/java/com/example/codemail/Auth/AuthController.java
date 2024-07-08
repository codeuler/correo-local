package com.example.codemail.Auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {

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

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "registro")
    public ResponseEntity<AuthResponse> registro(
            @RequestBody RegisterRequest request
    ) {
        //Implementar la busqueda de email
        return ResponseEntity.ok(authService.registro(request));
    }

}
