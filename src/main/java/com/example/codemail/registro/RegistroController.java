package com.example.codemail.registro;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistroController {

    @GetMapping("/registro")
    public String registroTemplate() {
        return "registro/registro";
    }

}
