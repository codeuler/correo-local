package com.example.codemail.auth;

import com.example.codemail.jwt.JwtService;
import com.example.codemail.folder.CarpetasDefecto;
import com.example.codemail.folder.FolderService;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioMapper;
import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final FolderService folderService;

    public AuthService(UsuarioMapper usuarioMapper, UsuarioRepository usuarioRepository, JwtService jwtService, AuthenticationManager authenticationManager, FolderService folderService) {
        this.usuarioMapper = usuarioMapper;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.folderService = folderService;
    }

    public ResponseEntity<AuthResponse> tryLogin(LoginRequest loginRequest) throws AuthNoValidException {
        try {
            return ResponseEntity.ok(login(loginRequest));
        } catch (AuthenticationException authenticationManager) {
            throw new AuthNoValidException("No se ha podido loguear");
        }
    }

    public ResponseEntity<AuthResponse> tryRegistro(RegisterRequest registerRequest) throws AuthRegistrerException {
        /*
         * En caso de que el correo que se ingresa exista, se devolverá un código de error 409
         * de lo contrario se creará el usuario en la base de datos
         */
        if (buscarUsuario(registerRequest.correo()).isPresent()) {
            throw new AuthRegistrerException("El usuario ya existe");
        } else {
            return ResponseEntity.ok(registro(registerRequest));
        }

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
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
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
        // A todos los usuarios se les crea una carpeta de Entrada y Enviados
        folderService.crearFolder(usuario, CarpetasDefecto.ENTRADA.getNombreCarpeta());
        folderService.crearFolder(usuario, CarpetasDefecto.ENVIADOS.getNombreCarpeta());
        return new AuthResponse(jwtService.getToken(usuario));
    }

    public Optional<Usuario> buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
