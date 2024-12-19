package com.example.codemail.autenticacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PeticionRegistro(
        @NotBlank(
                message = "El nombre no debe estar vacío"
        )
        @Size(
                min = 2,
                max = 15,
                message = "El nombre debe tener como mínimo 2 letras y como máximo 15"
        )
        String nombre,
        @NotBlank(
                message = "El nombre no debe estar vacío"
        )
        @Size(
                min = 2,
                max = 15,
                message = "El nombre debe tener como mínimo 2 letras y como máximo 15"
        )
        String apellido,
        @Pattern(
                regexp = "^[a-zA-Z0-9]{8,30}@learncode\\.local$",
                message = "El nombre del usuario solo puede contener letras y/o números, con un tamaño de entre 8 y 30 " +
                        "caracteres, a demás se debe agregar el arroba junto con el dominio '@learncode.local'"
        )
        String correo,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,30}$",
                message = "La password debe tener una longitud de entre 8 y 30 caracteres, a demás, poseer como mínimo " +
                        "cada uno de los siguientes parametros: minuscula, mayuscula, numero y caracter especial"
        )
        String password
) {
}
