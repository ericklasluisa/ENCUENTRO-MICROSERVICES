package ec.edu.espe.ms_authentication.service;

import ec.edu.espe.ms_authentication.dto.UsuarioInfoDto;
import ec.edu.espe.ms_authentication.model.Organizador;
import ec.edu.espe.ms_authentication.model.Usuario;
import ec.edu.espe.ms_authentication.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioInfoDto> obtenerUsuariosPorIds(List<Long> idsUsuarios) {
        List<Usuario> usuarios = usuarioRepository.findAllById(idsUsuarios);
        return usuarios.stream()
                .map(this::convertirAUsuarioInfoDto)
                .collect(Collectors.toList());
    }

    public UsuarioInfoDto obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirAUsuarioInfoDto(usuario);
    }

    private UsuarioInfoDto convertirAUsuarioInfoDto(Usuario usuario) {
        String rol = usuario instanceof Organizador ? "ORGANIZADOR" : "ASISTENTE";
        return new UsuarioInfoDto(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                rol
        );
    }
}
