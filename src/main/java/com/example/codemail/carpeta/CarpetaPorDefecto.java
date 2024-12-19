package com.example.codemail.carpeta;

/**
 * Este enum tiene la finalidad de estandarizar los nombres de las carpetas cuando son creadas para un nuevo usuario
 */
public enum CarpetaPorDefecto {
    ENTRADA("Entrada"),
    ENVIADOS("Enviados");

    private final String nombreCarpeta;

    CarpetaPorDefecto(String nombreCarpeta) {
        this.nombreCarpeta = nombreCarpeta;
    }
    public String getNombreCarpeta() {
        return nombreCarpeta;
    }
}
