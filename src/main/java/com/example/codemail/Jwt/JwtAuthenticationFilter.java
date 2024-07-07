package com.example.codemail.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpHeaders;

/**
 * Filtro personalizado para JWt
 */
/*
 * OncePerRequestFilter se utiliza para crear filtros personalizados, de esta manera el filtro se ejecuta por cada
 * solicitud http
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Obtener el token del request
        final String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);

    }

    /**
     * Permite obtener el token que provenga de una petición http
     * @param request solicitud http
     * @return
     */
    private String getTokenFromRequest(HttpServletRequest request) {
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
