package com.example.codemail.mensajepropietario;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.folder.Folder;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import com.example.codemail.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MensajePropietarioService extends UsuarioService implements RequestTokenExtractor {
    private final MensajePropietarioRepository mensajePropietarioRepository;
    private final MensajePropietarioMapper mensajePropietarioMapper;

    public MensajePropietarioService(MensajePropietarioRepository mensajePropietarioRepository, MensajePropietarioMapper mensajePropietarioMapper, JwtService jwtService, UsuarioRepository usuarioRepository) {
        super(jwtService, usuarioRepository);
        this.mensajePropietarioRepository = mensajePropietarioRepository;
        this.mensajePropietarioMapper = mensajePropietarioMapper;
    }

    public void guardarMensajePropietario(MensajePropietario mensajePropietario) {
        mensajePropietarioRepository.save(mensajePropietario);
    }

    public ResponseEntity<?> obtenerMensajes(HttpServletRequest request, String nombreFolder) {
        Usuario usuario = getUsuario(request);

        //Obtener el folder especifico del usuario
        Optional<Folder> carpetaOptional = usuario.getFolders()
                .stream()
                .filter(folder -> folder.getNombre().equals(nombreFolder))
                .findFirst();

        if (carpetaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder " + nombreFolder + " no existe");
        }

        Folder carpeta = carpetaOptional.get();
        return ResponseEntity.ok(carpeta
                .getMensajes()
                .stream()
                .map(mensaje -> mensajePropietarioRepository.findByUsuarioAndMensaje(usuario,mensaje))
                .flatMap(Optional::stream)
                .map(mensajePropietarioMapper::toMensajePropietarioEntrega)
                .collect(Collectors.toSet())
        );
    }

    public ResponseEntity<?> revisarMensaje(HttpServletRequest request, MensajePropietarioRevisar mensajePropietarioRevisar) {
        Usuario usuario = getUsuario(request);
        return mensajePropietarioRepository.findByIdAndUsuario(mensajePropietarioRevisar.idMensajePropietario(),usuario)
                .map(mensajePropietario -> {
                    mensajePropietario.setRevisado(true);
                    mensajePropietarioRepository.save(mensajePropietario);
                    return ResponseEntity.ok("Mensaje revisado con exito");
                }).orElseGet(()->ResponseEntity.status(HttpStatus.CONFLICT).body("No se ha encontrado dicho mensaje"));
    }
}
