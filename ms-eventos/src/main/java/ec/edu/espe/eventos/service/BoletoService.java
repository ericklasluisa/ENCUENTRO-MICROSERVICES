package ec.edu.espe.eventos.service;

import ec.edu.espe.eventos.client.UsuarioClient;
import ec.edu.espe.eventos.dto.BoletoDto;
import ec.edu.espe.eventos.dto.UsuarioInfoDto;
import ec.edu.espe.eventos.model.Boleto;
import ec.edu.espe.eventos.model.Evento;
import ec.edu.espe.eventos.repository.BoletoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    @Lazy
    private EventoService eventoService;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private NotificacionProducer notificacionProducer;

    public void crearBoletos(BoletoDto boletoDtos) {
        try {
            // Validaciones previas
            validarAsistenteExiste(boletoDtos.getIdAsistente());
            validarEventoParaBoleto(boletoDtos.getIdEvento());
            
            Integer cantidadBoletos = boletoDtos.getCantidadBoletos();
            
            // Verificar disponibilidad antes de crear múltiples boletos
            int disponibilidad = eventoService.verificarDisponibilidad(boletoDtos.getIdEvento());
            if (disponibilidad < cantidadBoletos) {
                throw new IllegalArgumentException("Solo hay " + disponibilidad + " boletos disponibles. Solicitados: " + cantidadBoletos);
            }
            
            Evento evento = eventoService.obtenerEventoPorId(boletoDtos.getIdEvento());

            for (int i = 0; i < cantidadBoletos; i++) {
                Boleto boleto = new Boleto();
                boleto.setEstado("ACTIVO");
                boleto.setFechaCompra(LocalDateTime.now());
                boleto.setIdAsistente(boletoDtos.getIdAsistente());
                boleto.setEvento(evento);

                boletoRepository.save(boleto);
            }
            
            // Enviar notificación de compra de boletos
            notificacionProducer.enviarNotificacionCompraBoleto(evento, boletoDtos.getIdAsistente(), cantidadBoletos);
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al crear boletos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear boletos: " + e.getMessage(), e);
        }
    }

    public Boleto obtenerBoletoPorId(Long idBoleto) {
        try {
            if (idBoleto == null) {
                throw new IllegalArgumentException("El id del boleto no puede ser nulo");
            }

            return boletoRepository.findById(idBoleto)
                    .orElseThrow(() -> new IllegalArgumentException("Boleto no encontrado con ID: " + idBoleto));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener boleto por ID: " + e.getMessage(), e);
        }
    }

    public int contarBoletosPorEvento(Long idEvento) {
        try {
            if (idEvento == null) {
                throw new IllegalArgumentException("El id del evento no puede ser nulo");
            }
            return boletoRepository.countByEventoIdEvento(idEvento);
        } catch (Exception e) {
            throw new RuntimeException("Error al contar boletos por evento: " + e.getMessage(), e);
        }
    }

    public List<Long> obtenerIdsAsistentesPorEvento(Long idEvento) {
        try {
            if (idEvento == null) {
                throw new IllegalArgumentException("El id del evento no puede ser nulo");
            }
            return boletoRepository.findIdsAsistentesByEventoIdEvento(idEvento);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener IDs de asistentes por evento: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el asistente existe en el microservicio de autenticación
     * @param idAsistente ID del asistente a validar
     * @throws IllegalArgumentException si el asistente no existe o hay error en la comunicación
     */
    private void validarAsistenteExiste(Long idAsistente) {
        try {
            if (idAsistente == null) {
                throw new IllegalArgumentException("El ID del asistente no puede ser nulo");
            }

            UsuarioInfoDto usuario = usuarioClient.obtenerUsuarioPorId(idAsistente);
            
            if (usuario == null) {
                throw new IllegalArgumentException("El asistente con ID " + idAsistente + " no existe");
            }
            
            // Validar que el usuario sea realmente un asistente
            if (!"ASISTENTE".equalsIgnoreCase(usuario.getRol())) {
                throw new IllegalArgumentException("El usuario con ID " + idAsistente + " no es un asistente");
            }
            
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Error al validar asistente: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el evento existe y está disponible para compra de boletos
     * @param idEvento ID del evento a validar
     * @throws IllegalArgumentException si el evento no está disponible
     */
    private void validarEventoParaBoleto(Long idEvento) {
        try {
            if (idEvento == null) {
                throw new IllegalArgumentException("El ID del evento no puede ser nulo");
            }

            Evento evento = eventoService.obtenerEventoPorId(idEvento);
            
            if (!"ACTIVO".equalsIgnoreCase(evento.getEstado())) {
                throw new IllegalArgumentException("El evento no está activo para venta de boletos");
            }
            
            // Verificar que el evento tiene aforo disponible
            if (evento.getAforo() <= 0) {
                throw new IllegalArgumentException("El evento no tiene aforo disponible");
            }
            
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Error al validar evento: " + e.getMessage(), e);
        }
    }
}

