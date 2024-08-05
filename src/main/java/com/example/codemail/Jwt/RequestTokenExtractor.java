package com.example.codemail.Jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public interface RequestTokenExtractor {
    /**
     * Permite obtener el token que provenga de una petición http
     * @param request solicitud http
     * @return String del token creado
     */
    default String getTokenFromRequest(HttpServletRequest request) {
        //Del header se trata de encontrar el apartado de autenticación
        final String authHeader = request.getHeader("Authorization");

        /*
         * El encabezado que se está buscando debe comenzar con la palabra "Bearer", a demás buscamos extraer el token
         * del String sin la cade "Bearer "
         *
         * StringUtils.hasText(authHeader) verifica si realmente la variable contiene texto o no
         */

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            //A partir de la posición 7, que es donde termina la cadena "Bearer ", hasta el final es donde se encuentra el token
            return authHeader.substring(7);
        }
        //De no encontrarse la cadena de "Bearer " se retorna null, es decir, el token no existe en el request
        return null;
    }
}
