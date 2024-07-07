package com.example.codemail.Auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/registro")
    public String registroTemplate() {
        return "registro/registro";
    }
}
