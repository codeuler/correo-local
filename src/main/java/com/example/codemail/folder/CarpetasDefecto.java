package com.example.codemail.folder;

/**
 * Este enum tiene la finalidad de estandarizar los nombres de las carpetas cuando son creadas para un nuevo usuario
 */
public enum CarpetasDefecto {
    ENTRADA("Entrada"),
    ENVIADOS("Enviados");

    private final String nombreCarpeta;

    CarpetasDefecto(String nombreCarpeta) {
        this.nombreCarpeta = nombreCarpeta;
    }
    public String getNombreCarpeta() {
        return nombreCarpeta;
    }
}
