package ec.edu.espe.eventos.controller;

import ec.edu.espe.eventos.dto.EventoDto;
import ec.edu.espe.eventos.dto.UsuarioInfoDto;
import ec.edu.espe.eventos.model.Evento;
import ec.edu.espe.eventos.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping
    public ResponseEntity<?> crearEvento(@RequestBody EventoDto eventoDto) {
        try {
            Evento evento = eventoService.convertirDtoAEntidad(eventoDto);
            Evento eventoCreado = eventoService.crearEvento(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(eventoCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear evento: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listarEventos() {
        try {
            List<Evento> eventos = eventoService.listarEventos();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar eventos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEventoPorId(@PathVariable Long id) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(id);
            return ResponseEntity.ok(evento);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener evento: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEvento(@PathVariable Long id, @RequestBody EventoDto eventoDto) {
        try {
            Evento eventoActualizado = eventoService.convertirDtoAEntidad(eventoDto);
            Evento evento = eventoService.actualizarEvento(id, eventoActualizado);
            return ResponseEntity.ok(evento);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar evento: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvento(@PathVariable Long id) {
        try {
            eventoService.eliminarEvento(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar evento: " + e.getMessage());
        }
    }

    @GetMapping("/disponibilidad/{idEvento}")
    public ResponseEntity<?> verificarDisponibilidad(@PathVariable Long idEvento) {
        try {
            int disponibilidad = eventoService.verificarDisponibilidad(idEvento);
            return ResponseEntity.ok(disponibilidad);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al verificar disponibilidad: " + e.getMessage());
        }
    }

    @GetMapping("/asistentes/{idEvento}")
    public ResponseEntity<?> listarAsistentes(@PathVariable Long idEvento) {
        try {
            List<UsuarioInfoDto> asistentes = eventoService.listarAsistentes(idEvento);
            return ResponseEntity.ok(asistentes);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar asistentes: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard/mis-eventos/{idOrganizador}")
    public ResponseEntity<?> obtenerMisEventos(@PathVariable Long idOrganizador) {
        try {
            List<Evento> misEventos = eventoService.obtenerMisEventos(idOrganizador);
            return ResponseEntity.ok(misEventos);
        } catch (UnsupportedOperationException ex) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Funcionalidad no implementada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener mis eventos: " + e.getMessage());
        }
    }
}
