package ec.edu.espe.eventos.client;

import ec.edu.espe.eventos.dto.UsuarioInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ms-authentication", url = "http://localhost:8080")
public interface UsuarioClient {

    @PostMapping("/usuarios/lista")
    List<UsuarioInfoDto> obtenerUsuariosPorIds(@RequestBody List<Long> idsUsuarios);

    @GetMapping("/usuarios/{id}")
    UsuarioInfoDto obtenerUsuarioPorId(@PathVariable("id") Long id);
}
