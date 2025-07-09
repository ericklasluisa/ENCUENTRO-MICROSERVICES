package ec.edu.espe.eventos.service;

import ec.edu.espe.eventos.dto.SolicitudPagoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PagoProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarSolicitudPago(SolicitudPagoDto solicitudPago) {
        try {
            log.info("Enviando solicitud de pago - Asistente: {} - Evento: {} - Monto: {}", 
                    solicitudPago.getIdAsistente(), solicitudPago.getIdEvento(), solicitudPago.getMontoTotal());

            rabbitTemplate.convertAndSend("solicitud_pagos.cola", solicitudPago);
            
            log.info("Solicitud de pago enviada exitosamente a ms-pagos");
        } catch (Exception e) {
            log.error("Error al enviar solicitud de pago: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar solicitud de pago", e);
        }
    }
}
