package com.example.codemail.inbox;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorInbox {

    @GetMapping("/inbox")
    public String inbox() {
        return "correo/correo";
    }
}
