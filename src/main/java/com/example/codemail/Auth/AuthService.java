package com.example.codemail.Auth;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioMapper;
import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioMapper usuarioMapper, UsuarioRepository usuarioRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioMapper = usuarioMapper;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(LoginRequest request) {
        /*
         * Cuando se llama al método authenticationManager.authenticate(...) en Spring Security, el
         * AuthenticationManager utiliza un AuthenticationProvider para autenticar al usuario.
         * al llamar authenticationManager.authenticate(...), estás delegando a Spring Security la responsabilidad de
         * autenticar al usuario utilizando la configuración que has proporcionado mediante el AuthenticationProvider.
         *
         * Cuando el método authenticate del AuthenticationManager no puede autenticar al usuario, generalmente lanza
         * una excepción del tipo AuthenticationException. Esta excepción se produce cuando las credenciales
         * proporcionadas no son válidas o cuando ocurre algún otro problema durante el proceso de autenticación.
         */
        authenticationManager.authenticate(
                /*
                 * UsernamePasswordAuthenticationToken es un objeto en Spring Security que representa un intento de
                 * autenticación mediante un nombre de usuario y una contraseña. Este token es utilizado por el
                 * AuthenticationManager para verificar las credenciales del usuario durante el proceso de
                 * autenticación.
                 */
                new UsernamePasswordAuthenticationToken(request.username(),request.password())
        );
        //Buscar al usuario en la base de datos
        UserDetails usuario = usuarioRepository.findByEmail(request.username()).orElseThrow();
        //Crear el token para el usuario
        String token = jwtService.getToken(usuario);
        //Retornar el token
        return new AuthResponse(token);
    }

    public AuthResponse registro(RegisterRequest request) {
        Usuario usuario = usuarioMapper.toUsuario(request);
        usuarioRepository.save(usuario);
        return new AuthResponse(jwtService.getToken(usuario));
    }
}
