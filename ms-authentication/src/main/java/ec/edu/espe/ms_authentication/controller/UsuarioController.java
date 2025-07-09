package ec.edu.espe.ms_authentication.controller;

import ec.edu.espe.ms_authentication.dto.UsuarioInfoDto;
import ec.edu.espe.ms_authentication.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/lista")
    public ResponseEntity<List<UsuarioInfoDto>> obtenerUsuariosPorIds(@RequestBody List<Long> idsUsuarios) {
        try {
            List<UsuarioInfoDto> usuarios = usuarioService.obtenerUsuariosPorIds(idsUsuarios);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioInfoDto> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            UsuarioInfoDto usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
