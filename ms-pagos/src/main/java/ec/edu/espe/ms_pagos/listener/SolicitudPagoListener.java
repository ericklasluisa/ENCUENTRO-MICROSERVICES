package ec.edu.espe.ms_pagos.listener;

import ec.edu.espe.ms_pagos.dto.RespuestaPagoDto;
import ec.edu.espe.ms_pagos.dto.SolicitudPagoDto;
import ec.edu.espe.ms_pagos.service.PagoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SolicitudPagoListener {

    @Autowired
    private PagoService pagoService;

    @RabbitListener(queues = "solicitud_pagos.cola")
    public void procesarSolicitudPago(@Payload SolicitudPagoDto solicitud) {
        log.info("Solicitud de pago recibida - Asistente: {} - Evento: {} - Cantidad: {} - Monto: {}", 
                solicitud.getIdAsistente(), solicitud.getIdEvento(), solicitud.getCantidadBoletos(), solicitud.getMontoTotal());
        
        try {
            // Crear solicitud de pago de forma asíncrona
            RespuestaPagoDto respuesta = pagoService.crearSolicitudPago(solicitud);
            
            log.info("Solicitud de pago procesada exitosamente - ID: {} - Estado: {}", 
                    respuesta.getIdPago(), respuesta.getEstadoPago());
                    
        } catch (Exception e) {
            log.error("Error al procesar solicitud de pago - Asistente: {} - Evento: {}", 
                    solicitud.getIdAsistente(), solicitud.getIdEvento(), e);
        }
    }
}
