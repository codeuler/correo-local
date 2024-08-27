package com.example.codemail.folder;

import com.example.codemail.Jwt.JwtService;
import com.example.codemail.Jwt.RequestTokenExtractor;
import com.example.codemail.usuario.Usuario;
import com.example.codemail.usuario.UsuarioRepository;
import com.example.codemail.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FolderService extends UsuarioService implements RequestTokenExtractor {
    private final FolderRepository folderRepository;
    private final FolderMapper folderMapper;

    public FolderService(FolderRepository folderRepository, JwtService jwtService, UsuarioRepository usuarioRepository, FolderMapper folderMapper) {
        super(jwtService, usuarioRepository);
        this.folderRepository = folderRepository;
        this.folderMapper = folderMapper;
    }

    public ResponseEntity<?> getAll(HttpServletRequest request) {
        Usuario usuario = getUsuario(request);
        Set<FolderRespuesta> folders = usuario.getFolders()
                .stream()
                .map(folderMapper::toFolderRespuesta)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(folders);
    }

    public Optional<Folder> getFolder(HttpServletRequest request, String folder) {
        return folderRepository.findByNombreAndPropietario(folder,getUsuario(request));
    }

    public ResponseEntity<?> crearFolder(HttpServletRequest request, FolderGuardar folderGuardar) {
        Usuario usuario = getUsuario(request);
        if (buscarFolderRepetido(usuario,folderGuardar.nombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder ya existe");
        } else {
            folderRepository.save(folderMapper.toFolder(usuario, folderGuardar.nombre()));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    private boolean buscarFolderRepetido(Usuario usuario, String nombreFolder) {
        Optional<Folder> carpeta = usuario.getFolders()
                .stream()
                .filter(
                        folder -> folder.getNombre().equals(nombreFolder)
                ).findFirst();
        return carpeta.isPresent();
    }

    public void crearFolder(Usuario usuario, String nombreFolder) {
        folderRepository.save(folderMapper.toFolder(usuario, nombreFolder));
    }

    public ResponseEntity<?> actualizarFolder(HttpServletRequest request, String nombreCarpeta, FolderGuardar folderGuardar) {
        Optional<Folder> optionalFolder = getFolder(request, nombreCarpeta);
        if (optionalFolder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Folder folder = optionalFolder.get();
        if (buscarFolderRepetido(getUsuario(request), folderGuardar.nombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El folder ya existe");
        }
        folder.setNombre(folderGuardar.nombre());
        folderRepository.save(folder);
        return ResponseEntity.ok().body(folderMapper.toFolderRespuesta(folder));
    }
}
