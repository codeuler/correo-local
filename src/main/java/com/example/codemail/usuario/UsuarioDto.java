package com.example.codemail.usuario;

import jakarta.validation.constraints.*;

public record UsuarioDto(
        @NotBlank
        @Size(min = 2, max = 15)
        String nombre,
        @NotBlank
        @Size(min = 2, max = 15)
        String apellido,
        @Email(
                regexp = "^[a-zA-Z0-9]{5,30}@learncode.local$"
        )
        String correo,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,30}$"
        )
        String password
) {
}
