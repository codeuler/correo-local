package com.example.codemail.autenticacion;

import com.example.codemail.jwt.ServicioJwt;
import com.example.codemail.carpeta.CarpetaPorDefecto;
import com.example.codemail.carpeta.ServicioCarpeta;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioMapeador;
import com.example.codemail.usuario.RepositorioUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServicioAutenticacion {
    private final UsuarioMapeador usuarioMapeador;
    private final RepositorioUsuario repositorioUsuario;
    private final ServicioJwt servicioJwt;
    private final AuthenticationManager authenticationManager;
    private final ServicioCarpeta servicioCarpeta;

    public ServicioAutenticacion(UsuarioMapeador usuarioMapeador, RepositorioUsuario repositorioUsuario, ServicioJwt servicioJwt, AuthenticationManager authenticationManager, ServicioCarpeta servicioCarpeta) {
        this.usuarioMapeador = usuarioMapeador;
        this.repositorioUsuario = repositorioUsuario;
        this.servicioJwt = servicioJwt;
        this.authenticationManager = authenticationManager;
        this.servicioCarpeta = servicioCarpeta;
    }

    public ResponseEntity<RespuestaAutenticacion> tryLogin(PeticionLogin peticionLogin) throws AutenticacionNoValidaExcepcion {
        try {
            return ResponseEntity.ok(login(peticionLogin));
        } catch (AuthenticationException authenticationManager) {
            throw new AutenticacionNoValidaExcepcion("No se ha podido loguear");
        }
    }

    public ResponseEntity<RespuestaAutenticacion> tryRegistro(PeticionRegistro peticionRegistro) throws ErrorRegistroExcepcion {
        /*
         * En caso de que el correo que se ingresa exista, se devolverá un código de error 409
         * de lo contrario se creará el usuario en la base de datos
         */
        if (buscarUsuario(peticionRegistro.correo()).isPresent()) {
            throw new ErrorRegistroExcepcion("El usuario ya existe");
        } else {
            return ResponseEntity.ok(registro(peticionRegistro));
        }

    }

    public RespuestaAutenticacion login(PeticionLogin request) {
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
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        //Buscar al usuario en la base de datos
        UserDetails usuario = repositorioUsuario.findByEmail(request.username()).orElseThrow();
        //Crear el token para el usuario
        String token = servicioJwt.getToken(usuario);
        //Retornar el token
        return new RespuestaAutenticacion(token);
    }

    public RespuestaAutenticacion registro(PeticionRegistro request) {
        Usuario usuario = usuarioMapeador.toUsuario(request);
        repositorioUsuario.save(usuario);
        // A todos los usuarios se les crea una carpeta de Entrada y Enviados
        servicioCarpeta.crearFolder(usuario, CarpetaPorDefecto.ENTRADA.getNombreCarpeta());
        servicioCarpeta.crearFolder(usuario, CarpetaPorDefecto.ENVIADOS.getNombreCarpeta());
        return new RespuestaAutenticacion(servicioJwt.getToken(usuario));
    }

    public Optional<Usuario> buscarUsuario(String email) {
        return repositorioUsuario.findByEmail(email);
    }
}
