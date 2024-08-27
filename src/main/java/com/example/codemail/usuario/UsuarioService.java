package com.example.codemail.usuario;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements RequestTokenExtractor {
    protected final JwtService jwtService;
    protected final UsuarioRepository usuarioRepository;

    public UsuarioService(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    protected Usuario getUsuario(HttpServletRequest request) {
        //Buscar el username del usuario que envio la petición
        String username = jwtService.getUsernameFromToken(getTokenFromRequest(request));
        return usuarioRepository.findByEmail(username).orElse(null);
    }
}
