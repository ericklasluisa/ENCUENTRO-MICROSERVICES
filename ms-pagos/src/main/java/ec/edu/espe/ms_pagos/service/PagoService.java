package ec.edu.espe.ms_pagos.service;

import ec.edu.espe.ms_pagos.dto.AprobacionPagoDto;
import ec.edu.espe.ms_pagos.dto.PagoDto;
import ec.edu.espe.ms_pagos.dto.RespuestaPagoDto;
import ec.edu.espe.ms_pagos.dto.SolicitudPagoDto;
import ec.edu.espe.ms_pagos.model.Pago;
import ec.edu.espe.ms_pagos.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private NotificacionProducer notificacionProducer;

    @Transactional
    public RespuestaPagoDto crearSolicitudPago(SolicitudPagoDto solicitud) {
        try {
            log.info("Creando solicitud de pago por transferencia para asistente: {} - Evento: {}", 
                    solicitud.getIdAsistente(), solicitud.getIdEvento());

            // Validar solicitud
            validarSolicitudPago(solicitud);

            // Crear registro de pago pendiente
            Pago pago = crearRegistroPago(solicitud);
            pago = pagoRepository.save(pago);

            // Enviar notificación al organizador
            notificacionProducer.enviarNotificacionSolicitudPago(
                pago.getIdOrganizador(), 
                pago.getIdPago().toString(),
                pago.getMontoTotal().doubleValue(),
                "Nueva solicitud de pago por transferencia para evento ID: " + pago.getIdEvento()
            );

            // Enviar notificación al asistente confirmando recepción
            notificacionProducer.enviarNotificacionPagoPendiente(
                pago.getIdAsistente(),
                pago.getIdPago().toString(),
                "Tu solicitud de pago está pendiente de aprobación por el organizador"
            );

            RespuestaPagoDto respuesta = new RespuestaPagoDto();
            respuesta.setIdPago(pago.getIdPago());
            respuesta.setIdAsistente(pago.getIdAsistente());
            respuesta.setIdEvento(pago.getIdEvento());
            respuesta.setCantidadBoletos(pago.getCantidadBoletos());
            respuesta.setMontoTotal(pago.getMontoTotal());
            respuesta.setEstadoPago("PENDIENTE");
            respuesta.setExitoso(true);
            respuesta.setMensaje("Solicitud de pago creada exitosamente. Pendiente de aprobación del organizador.");

            log.info("Solicitud de pago creada. ID: {} - Estado: PENDIENTE", pago.getIdPago());

            return respuesta;

        } catch (Exception e) {
            log.error("Error al crear solicitud de pago: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al crear la solicitud de pago: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RespuestaPagoDto procesarAprobacion(AprobacionPagoDto aprobacion) {
        try {
            log.info("Procesando aprobación/rechazo de pago ID: {}", aprobacion.getIdPago());

            Pago pago = pagoRepository.findById(aprobacion.getIdPago())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Pago no encontrado con ID: " + aprobacion.getIdPago()));

            // Validar que esté pendiente
            if (!"PENDIENTE".equals(pago.getEstadoPago())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "El pago ya ha sido procesado. Estado actual: " + pago.getEstadoPago());
            }

            // Actualizar pago
            if ("APROBAR".equals(aprobacion.getAccion())) {
                pago.setEstadoPago("APROBADO");
                pago.setFechaAprobacion(LocalDateTime.now());
                
                // Notificar aprobación al asistente
                notificacionProducer.enviarNotificacionPagoAprobado(
                    pago.getIdAsistente(),
                    pago.getIdPago().toString(),
                    pago.getMontoTotal().doubleValue(),
                    "Tu pago ha sido aprobado por el organizador"
                );

                // Notificar a ms-eventos para generar boletos
                notificacionProducer.enviarRespuestaPagoAprobado(pago);
                
            } else if ("RECHAZAR".equals(aprobacion.getAccion())) {
                pago.setEstadoPago("RECHAZADO");
                pago.setFechaAprobacion(LocalDateTime.now());
                
                // Notificar rechazo al asistente
                notificacionProducer.enviarNotificacionPagoRechazado(
                    "Tu pago ha sido rechazado por el organizador",
                    "Evento ID: " + pago.getIdEvento()
                );
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Acción inválida. Use 'APROBAR' o 'RECHAZAR'");
            }

            pago = pagoRepository.save(pago);

            RespuestaPagoDto respuesta = new RespuestaPagoDto();
            respuesta.setIdPago(pago.getIdPago());
            respuesta.setIdAsistente(pago.getIdAsistente());
            respuesta.setIdEvento(pago.getIdEvento());
            respuesta.setCantidadBoletos(pago.getCantidadBoletos());
            respuesta.setMontoTotal(pago.getMontoTotal());
            respuesta.setEstadoPago(pago.getEstadoPago());
            respuesta.setExitoso("APROBADO".equals(pago.getEstadoPago()));
            respuesta.setMensaje("Pago " + pago.getEstadoPago().toLowerCase() + " exitosamente");

            log.info("Pago procesado. ID: {} - Estado: {}", pago.getIdPago(), pago.getEstadoPago());

            return respuesta;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al procesar aprobación: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al procesar la aprobación: " + e.getMessage(), e);
        }
    }

    public List<PagoDto> obtenerPagosPendientesPorOrganizador(Long idOrganizador) {
        try {
            List<Pago> pagos = pagoRepository.findByIdOrganizadorAndEstadoPago(idOrganizador, "PENDIENTE");
            return pagos.stream()
                    .map(this::convertirADto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener pagos pendientes por organizador: {}", idOrganizador, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al obtener pagos pendientes", e);
        }
    }

    public List<PagoDto> obtenerPagosPorAsistente(Long idAsistente) {
        try {
            List<Pago> pagos = pagoRepository.findByIdAsistente(idAsistente);
            return pagos.stream()
                    .map(this::convertirADto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener pagos por asistente: {}", idAsistente, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al obtener pagos del asistente", e);
        }
    }

    public List<PagoDto> obtenerPagosPorEvento(Long idEvento) {
        try {
            List<Pago> pagos = pagoRepository.findByIdEvento(idEvento);
            return pagos.stream()
                    .map(this::convertirADto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener pagos por evento: {}", idEvento, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al obtener pagos del evento", e);
        }
    }

    private void validarSolicitudPago(SolicitudPagoDto solicitud) {
        if (solicitud.getIdAsistente() == null) {
            throw new IllegalArgumentException("ID del asistente es requerido");
        }
        if (solicitud.getIdEvento() == null) {
            throw new IllegalArgumentException("ID del evento es requerido");
        }
        if (solicitud.getIdOrganizador() == null) {
            throw new IllegalArgumentException("ID del organizador es requerido");
        }
        if (solicitud.getCantidadBoletos() == null || solicitud.getCantidadBoletos() <= 0) {
            throw new IllegalArgumentException("Cantidad de boletos debe ser mayor a 0");
        }
        if (solicitud.getMontoTotal() == null || solicitud.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto total debe ser mayor a 0");
        }
    }

    private Pago crearRegistroPago(SolicitudPagoDto solicitud) {
        Pago pago = new Pago();
        pago.setIdAsistente(solicitud.getIdAsistente());
        pago.setIdEvento(solicitud.getIdEvento());
        pago.setIdOrganizador(solicitud.getIdOrganizador());
        pago.setCantidadBoletos(solicitud.getCantidadBoletos());
        pago.setMontoTotal(solicitud.getMontoTotal());
        pago.setEstadoPago("PENDIENTE");
        
        return pago;
    }

    private PagoDto convertirADto(Pago pago) {
        PagoDto dto = new PagoDto();
        BeanUtils.copyProperties(pago, dto);
        return dto;
    }
}
