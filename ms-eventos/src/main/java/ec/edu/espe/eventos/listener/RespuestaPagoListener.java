package ec.edu.espe.eventos.listener;

import ec.edu.espe.eventos.dto.BoletoDto;
import ec.edu.espe.eventos.dto.RespuestaPagoDto;
import ec.edu.espe.eventos.service.BoletoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RespuestaPagoListener {

    @Autowired
    private BoletoService boletoService;

    @RabbitListener(queues = "respuesta_pagos.cola")
    public void procesarRespuestaPago(@Payload RespuestaPagoDto respuesta) {
        log.info("Respuesta de pago recibida - ID Pago: {} - Estado: {} - Exitoso: {}", 
                respuesta.getIdPago(), respuesta.getEstadoPago(), respuesta.isExitoso());
        
        try {
            // Solo procesar si el pago fue aprobado
            if (respuesta.isExitoso() && "APROBADO".equals(respuesta.getEstadoPago())) {
                log.info("Pago aprobado, creando boletos para asistente: {} - Evento: {} - Cantidad: {}", 
                        respuesta.getIdAsistente(), respuesta.getIdEvento(), respuesta.getCantidadBoletos());
                
                // Crear los boletos
                BoletoDto boletoDto = new BoletoDto();
                boletoDto.setIdAsistente(respuesta.getIdAsistente());
                boletoDto.setIdEvento(respuesta.getIdEvento());
                boletoDto.setCantidadBoletos(respuesta.getCantidadBoletos());
                
                boletoService.crearBoletos(boletoDto);
                
                log.info("Boletos creados exitosamente para pago ID: {}", respuesta.getIdPago());
            } else {
                log.warn("Pago no aprobado - ID: {} - Estado: {} - Mensaje: {}", 
                        respuesta.getIdPago(), respuesta.getEstadoPago(), respuesta.getMensaje());
            }
            
        } catch (Exception e) {
            log.error("Error al procesar respuesta de pago - ID: {}", respuesta.getIdPago(), e);
        }
    }
}
