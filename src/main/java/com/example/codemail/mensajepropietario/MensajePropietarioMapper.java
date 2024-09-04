package com.example.codemail.mensajepropietario;

import org.springframework.stereotype.Service;

@Service
public class MensajePropietarioMapper {
    public MensajePropietarioEntrega toMensajePropietarioEntrega(MensajePropietario mensajePropietario){
        return new MensajePropietarioEntrega(
                mensajePropietario.mensaje.getAsunto(),
                mensajePropietario.mensaje.getCuerpo(),
                mensajePropietario.mensaje.getFechaEnvio(),
                mensajePropietario.mensaje.getUsuario().getNombre() + " " + mensajePropietario.mensaje.getUsuario().getApellido(),
                mensajePropietario.getRevisado()
        );
    }
}
