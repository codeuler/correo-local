package com.example.codemail.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro personalizado para JWt
 */
/*
 * OncePerRequestFilter se utiliza para crear filtros personalizados, de esta manera el filtro se ejecuta por cada
 * solicitud http
 */
@Component
public class FiltroAutenticacionJwt extends OncePerRequestFilter {

    private final ServicioJwt servicioJwt;
    private final UserDetailsService userDetailsService;

    public FiltroAutenticacionJwt(ServicioJwt servicioJwt, UserDetailsService userDetailsService) {
        this.servicioJwt = servicioJwt;
        this.userDetailsService = userDetailsService;
    }
    // Es una convención en Spring Security para definir métodos que contienen la lógica principal de un filtro personalizado
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Obtener el token del request
        final String token = getTokenFromRequest(request);
        final String username;
        /*
         * Si token es null, el código llama a filterChain.doFilter(request, response);. Esto pasa la solicitud y la
         * respuesta al siguiente filtro en la cadena de filtros. En otras palabras, este filtro no realiza ninguna
         * acción adicional en esta solicitud y simplemente permite que el siguiente filtro (o el recurso final)
         * procese la solicitud.
         */
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        username = servicioJwt.getUsernameFromToken(token);
        /*
         * En aplicaciones que utilizan Spring Security (o algún otro mecanismo de seguridad similar), el contexto de
         * seguridad (SecurityContext) es un objeto que contiene los detalles de la autenticación y autorización del
         * usuario actualmente en sesión. SecurityContextHolder es la clase que proporciona métodos estáticos para
         * acceder y manipular este contexto de seguridad.
         *
         * Al usar getContext() se obtiene el contexto de seguridad actual: El contexto de seguridad contiene
         * información sobre el principal (usuario autenticado), detalles de la autenticación, roles y otras
         * configuraciones de seguridad relevantes.
         *
         * getAuthentication(): Este método dentro del contexto de seguridad (SecurityContext) devuelve el objeto
         * Authentication actualmente establecido en el contexto. El objeto Authentication representa los detalles de
         * la autenticación del usuario actual, como el nombre de usuario, credenciales (contraseña o token), roles y
         * autoridades.

         * == null: Esta parte de la expresión verifica si el objeto Authentication devuelto por getAuthentication() es
         * nulo. Si getAuthentication() devuelve null, significa que no hay ningún usuario autenticado en el contexto de
         *  seguridad actual.
         */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Busca en la base de datos por el username utilizando el método sobreescrito
            // ApplicationConfig::userDetailService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (servicioJwt.isTokenValid(token, userDetails)) {
                /*
                 * UsernamePasswordAuthenticationToken: Esta es una clase proporcionada por Spring Security que
                 * implementa la interfaz Authentication. Se utiliza para representar una solicitud de autenticación
                 * basada en nombre de usuario y contraseña en el contexto de Spring Security.
                 */
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        /*
                         * null: Este parámetro representa la contraseña del usuario. En este caso, se establece como
                         * null porque el usuario ya ha sido autenticado previamente y no se necesita proporcionar la
                         * contraseña nuevamente. En situaciones donde se está iniciando sesión o se necesita autenticar
                         * con una contraseña, este parámetro contendría la contraseña del usuario.
                         */
                        null,
                        //Obtener el rol del usuario
                        userDetails.getAuthorities()
                );
                /*
                 * se utiliza para establecer los detalles adicionales de autenticación en el objeto authentication de
                 * tipo UsernamePasswordAuthenticationToken.
                 */
                authentication.setDetails(
                        /*
                         * WebAuthenticationDetailsSource es una clase proporcionada por Spring Security que se utiliza
                         * para construir los detalles específicos de la autenticación web (WebAuthenticationDetails).
                         * Estos detalles incluyen información como la dirección IP del cliente, el agente de usuario
                         * del navegador y otros detalles del request HTTP.
                         */
                        new WebAuthenticationDetailsSource()
                                /*
                                 * Este método toma un objeto HttpServletRequest (request) como parámetro y construye
                                 * los detalles de la autenticación web (WebAuthenticationDetails) basados en este
                                 * request. Por lo tanto, buildDetails(request) genera un objeto
                                 * WebAuthenticationDetails con la información relevante del request HTTP actual.
                                 */
                                .buildDetails(request)
                );
                //Se agrega la autenticación del usuario al contexto actual
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        /*
         * El propósito de llamar a filterChain.doFilter(request, response); es permitir que la solicitud continúe su
         * camino a través de la cadena de filtros y, eventualmente, llegue al recurso final que debe procesar la
         * solicitud (por ejemplo, un servlet, un controlador de Spring MVC, etc.).
         */
        filterChain.doFilter(request, response);
    }
    /*
     * Permite obtener el token que provenga de una petición http
     */
    public String getTokenFromRequest(HttpServletRequest request) {
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
