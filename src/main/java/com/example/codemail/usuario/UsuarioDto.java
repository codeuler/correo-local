package com.example.codemail.usuario;

public record UsuarioDto(
        String nombre,
        String apellido,
        String correo,
        String password
) {
}
