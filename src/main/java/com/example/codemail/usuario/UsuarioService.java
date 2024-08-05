package com.example.codemail.usuario;

import com.example.codemail.folder.FolderService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final FolderService folderService;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder, FolderService folderService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.folderService = folderService;
    }

    public UsuarioDto create(UsuarioDto usuarioDto) {
        var usuario = usuarioMapper.toUsuario(usuarioDto);
        //Encriptar password
        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );
        usuarioRepository.save(usuario);

        // A todos los usuarios se les crea una carpeta de Entrada y Enviados
        folderService.crearFolder(usuario,"Entrada");
        folderService.crearFolder(usuario,"Enviados");
        return usuarioDto;
    }

}
