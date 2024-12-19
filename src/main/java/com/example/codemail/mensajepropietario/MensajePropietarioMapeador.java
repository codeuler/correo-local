package com.example.codemail.mensajepropietario;

import org.springframework.stereotype.Service;

@Service
public class MensajePropietarioMapeador {
    public MensajePropietarioRespuesta toMensajePropietarioEntrega(MensajePropietario mensajePropietario){
        return new MensajePropietarioRespuesta(
                mensajePropietario.mensaje.getId(),
                mensajePropietario.mensaje.getAsunto(),
                mensajePropietario.mensaje.getCuerpo(),
                mensajePropietario.mensaje.getFechaEnvio(),
                mensajePropietario.mensaje.getUsuario().getNombre() + " " + mensajePropietario.mensaje.getUsuario().getApellido(),
                mensajePropietario.getRevisado()
        );
    }
}
