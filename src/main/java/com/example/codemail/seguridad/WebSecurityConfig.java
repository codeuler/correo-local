package com.example.codemail.seguridad;

import com.example.codemail.Jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    /*
     * En Spring Security, SecurityFilterChain representa una cadena de filtros de seguridad que se aplica a las
     * solicitudes HTTP para proporcionar seguridad a nivel de aplicación, es decir, el metodo crear un filtro
     * personalizado para las solicitudes http
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(
                    (requests) -> requests
                            .requestMatchers("/registro","/registro/**","/usuarios","/login/**"
                                    ).permitAll()
                            .anyRequest().authenticated()
                ).formLogin(
                        (form) -> form.loginPage("/login").permitAll()
                )
                //Deshabilitar las sesiones
                .sessionManagement( sessionManger ->
                        sessionManger.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Definir nuestro propio proveedor de autenticación
                .authenticationProvider(authenticationProvider)
                /*
                 * Al llamar a addFilterBefore, estás indicando que jwtAuthenticationFilter debe ser agregado antes del
                 * filtro de autenticación de nombre de usuario y contraseña (UsernamePasswordAuthenticationFilter).
                 */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(
                    AbstractHttpConfigurer::disable
                )
        ;
        return http.build();
    }

}
