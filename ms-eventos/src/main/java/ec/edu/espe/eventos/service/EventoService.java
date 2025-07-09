package ec.edu.espe.eventos.service;

import ec.edu.espe.eventos.client.UsuarioClient;
import ec.edu.espe.eventos.dto.UsuarioInfoDto;
import ec.edu.espe.eventos.model.Evento;
import ec.edu.espe.eventos.repository.EventoRepository;
import ec.edu.espe.eventos.dto.EventoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    @Lazy
    private BoletoService boletoService;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private NotificacionProducer notificacionProducer;

    @Transactional
    public Evento crearEvento(Evento evento) {
        try {
            if (evento == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El evento no puede ser nulo");
            }
            
            // Validar que el organizador existe en el microservicio de autenticación
            if (evento.getIdOrganizador() != null) {
                validarOrganizadorExiste(evento.getIdOrganizador());
            }
            
            Evento eventoCreado = eventoRepository.save(evento);
            
            // Enviar notificación de creación de evento
            notificacionProducer.enviarNotificacionCreacionEvento(eventoCreado);
            
            return eventoCreado;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear evento: " + e.getMessage(), e);
        }
    }

    public List<Evento> listarEventos() {
        try {
            return eventoRepository.findAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar eventos: " + e.getMessage(), e);
        }
    }

    public Evento obtenerEventoPorId(Long id) {
        try {
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del evento no puede ser nulo");
            }
            return eventoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener evento: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Evento actualizarEvento(Long id, Evento eventoActualizado) {
        try {
            if (id == null || eventoActualizado == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
            }
            
            // Validar que el organizador existe en el microservicio de autenticación
            if (eventoActualizado.getIdOrganizador() != null) {
                validarOrganizadorExiste(eventoActualizado.getIdOrganizador());
            }
            
            Evento evento = obtenerEventoPorId(id);
            BeanUtils.copyProperties(eventoActualizado, evento, "idEvento");
            Evento eventoActualizadoGuardado = eventoRepository.save(evento);
            
            // Enviar notificación de actualización de evento
            notificacionProducer.enviarNotificacionActualizacionEvento(eventoActualizadoGuardado);
            
            return eventoActualizadoGuardado;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar evento: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void eliminarEvento(Long id) {
        try {
            Evento evento = obtenerEventoPorId(id);
            String nombreEvento = evento.getTitulo(); // Guardamos el nombre antes de eliminar
            
            eventoRepository.delete(evento);
            
            // Enviar notificación de eliminación de evento
            notificacionProducer.enviarNotificacionEliminacionEvento(nombreEvento);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar evento: " + e.getMessage(), e);
        }
    }

    public int verificarDisponibilidad(Long idEvento) {
        try {
            Evento evento = obtenerEventoPorId(idEvento);
            if (evento.getAforo() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay aforo disponible");
            }
            if (!"ACTIVO".equalsIgnoreCase(evento.getEstado())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El evento no está activo");
            }

            int boletosVendidos = boletoService.contarBoletosPorEvento(idEvento);
            int disponibilidad = evento.getAforo() - boletosVendidos;
            if (disponibilidad < 0) {
                disponibilidad = 0; // No puede haber disponibilidad negativa
            }
            return disponibilidad;

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al verificar disponibilidad: " + e.getMessage(), e);
        }
    }

    public List<Evento> obtenerMisEventos(Long idOrganizador) {
        try {
            if (idOrganizador == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del organizador no puede ser nulo");
            }
            
            // Validar que el organizador existe en el microservicio de autenticación
            validarOrganizadorExiste(idOrganizador);
            
            return eventoRepository.findAllByIdOrganizador(idOrganizador);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener eventos: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para convertir DTO a entidad
    public Evento convertirDtoAEntidad(EventoDto eventoDto) {
        try {
            if (eventoDto == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DTO de evento no puede ser nulo");
            }
            Evento evento = new Evento();
            BeanUtils.copyProperties(eventoDto, evento);
            return evento;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al convertir DTO a entidad: " + e.getMessage(), e);
        }
    }

    public List<UsuarioInfoDto> listarAsistentes(Long idEvento) {
        try {
            // Obtener IDs de asistentes del evento
            List<Long> idsAsistentes = boletoService.obtenerIdsAsistentesPorEvento(idEvento);

            if (idsAsistentes == null || idsAsistentes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay asistentes para el evento con ID: " + idEvento);
            }

            // Usar comunicación síncrona para obtener información de usuarios
            return usuarioClient.obtenerUsuariosPorIds(idsAsistentes);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar asistentes: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el organizador existe en el microservicio de autenticación
     * @param idOrganizador ID del organizador a validar
     * @throws ResponseStatusException si el organizador no existe o hay error en la comunicación
     */
    private void validarOrganizadorExiste(Long idOrganizador) {
        try {
            UsuarioInfoDto usuario = usuarioClient.obtenerUsuarioPorId(idOrganizador);
            
            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "El organizador con ID " + idOrganizador + " no existe");
            }
            
            // Validar que el usuario sea realmente un organizador
            if (!"ORGANIZADOR".equalsIgnoreCase(usuario.getRol())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "El usuario con ID " + idOrganizador + " no es un organizador");
            }
            
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                "Error al validar organizador: " + e.getMessage(), e);
        }
    }
}
