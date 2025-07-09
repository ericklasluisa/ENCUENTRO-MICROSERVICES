package ec.edu.espe.ms_pagos.service;

import ec.edu.espe.ms_pagos.dto.NotificacionDto;
import ec.edu.espe.ms_pagos.dto.RespuestaPagoDto;
import ec.edu.espe.ms_pagos.model.Pago;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificacionProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarNotificacionSolicitudPago(Long idOrganizador, String numeroTransaccion, Double monto, String descripcion) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setIdUsuario(idOrganizador);
            notificacion.setMensaje(String.format("Nueva solicitud de pago por transferencia. Transacción: %s por $%.2f. %s", 
                                                 numeroTransaccion, monto, descripcion));
            notificacion.setTipo("SOLICITUD_PAGO");
            notificacion.setAsunto("Nueva solicitud de pago pendiente");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de solicitud de pago enviada al organizador: {} - Transacción: {}", idOrganizador, numeroTransaccion);
        } catch (Exception e) {
            log.error("Error al enviar notificación de solicitud de pago: {}", numeroTransaccion, e);
        }
    }

    public void enviarNotificacionPagoPendiente(Long idAsistente, String numeroTransaccion, String mensaje) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setIdUsuario(idAsistente);
            notificacion.setMensaje(String.format("%s Número de transacción: %s", mensaje, numeroTransaccion));
            notificacion.setTipo("PAGO_PENDIENTE");
            notificacion.setAsunto("Solicitud de pago recibida");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de pago pendiente enviada al asistente: {} - Transacción: {}", idAsistente, numeroTransaccion);
        } catch (Exception e) {
            log.error("Error al enviar notificación de pago pendiente: {}", numeroTransaccion, e);
        }
    }

    public void enviarNotificacionPagoAprobado(Long idAsistente, String numeroTransaccion, Double monto, String mensaje) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setIdUsuario(idAsistente);
            notificacion.setMensaje(String.format("¡%s! Transacción: %s por $%.2f. Tus boletos han sido confirmados.", 
                                                 mensaje, numeroTransaccion, monto));
            notificacion.setTipo("PAGO_APROBADO");
            notificacion.setAsunto("Pago aprobado - Boletos confirmados");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de pago aprobado enviada al asistente: {} - Transacción: {}", idAsistente, numeroTransaccion);
        } catch (Exception e) {
            log.error("Error al enviar notificación de pago aprobado: {}", numeroTransaccion, e);
        }
    }

    public void enviarNotificacionPagoRechazado(String motivoRechazo, String evento) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setMensaje(String.format("Pago rechazado para el evento '%s'. Motivo: %s. Por favor, contacta con el organizador si tienes dudas.", 
                                                 evento, motivoRechazo));
            notificacion.setTipo("PAGO_RECHAZADO");
            notificacion.setAsunto("Pago rechazado");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de pago rechazado enviada para evento: {}", evento);
        } catch (Exception e) {
            log.error("Error al enviar notificación de pago rechazado para evento: {}", evento, e);
        }
    }

    public void enviarRespuestaPagoAprobado(Pago pago) {
        try {
            RespuestaPagoDto respuesta = new RespuestaPagoDto();
            respuesta.setIdPago(pago.getIdPago());
            respuesta.setEstadoPago(pago.getEstadoPago());
            respuesta.setExitoso(true);
            respuesta.setMensaje("Pago aprobado por el organizador");
            respuesta.setIdAsistente(pago.getIdAsistente());
            respuesta.setIdEvento(pago.getIdEvento());
            respuesta.setCantidadBoletos(pago.getCantidadBoletos());
            respuesta.setMontoTotal(pago.getMontoTotal());

            rabbitTemplate.convertAndSend("respuesta_pagos.cola", respuesta);
            
            log.info("Respuesta de pago aprobado enviada a ms-eventos. ID Pago: {} - Estado: {}", 
                    respuesta.getIdPago(), respuesta.getEstadoPago());
        } catch (Exception e) {
            log.error("Error al enviar respuesta de pago aprobado: {}", pago.getIdPago(), e);
        }
    }
}
