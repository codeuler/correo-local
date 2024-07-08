package com.example.codemail.Auth;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioMapper;
import com.example.codemail.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(UsuarioMapper usuarioMapper, UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioMapper = usuarioMapper;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        return null;
    }

    public AuthResponse registro(RegisterRequest request) {
        Usuario usuario = usuarioMapper.toUsuario(request);
        usuarioRepository.save(usuario);
        return new AuthResponse(jwtService.getToken(usuario));
    }
}
