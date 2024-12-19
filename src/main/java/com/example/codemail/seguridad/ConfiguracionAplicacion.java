package com.example.codemail.seguridad;

import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ConfiguracionAplicacion {

    private final UsuarioRepository usuarioRepository;

    public ConfiguracionAplicacion(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Bean
    //AuthenticationManager es una interfaz que permite la autenticación de usuarios proporcionando métodos para
    //validar credenciales
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /*
     * AuthenticationProvider ess una interfaz en Spring Security que define un contrato para autenticar a los usuarios
     * en función de ciertos criterios. Es fundamental en el proceso de autenticación de Spring Security, ya que permite
     * integrar diferentes fuentes de autenticación y estrategias personalizadas.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        /*
         * DaoAuthenticationProvider es una implementación concreta de AuthenticationProvider en Spring Security que
         * utiliza un UserDetailsService para cargar los detalles de usuario de una fuente de datos, como una base de
         * datos, y luego autenticar al usuario.
         */
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailService() {
        /*
         * El parámetro username en la lambda se refiere al parámetro del método loadUserByUsername de
         * UserDetailsService. Cuando Spring Security llama al método loadUserByUsername, pasa el nombre de usuario del
         * usuario que está intentando autenticarse. Así es como se une to do.
         * El usuario intenta autenticarse: Spring Security captura el nombre de usuario del formulario de inicio de
         * sesión. Spring Security llama a loadUserByUsername: Spring Security usa la implementación de
         * UserDetailsService proporcionada por tu método userDetailService.
         * La lambda es ejecutada: La lambda username -> ... se ejecuta con el nombre de usuario que Spring Security ha
         * pasado.
         */
        return username -> usuarioRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

}
