package ec.edu.espe.ms_pagos.controller;

import ec.edu.espe.ms_pagos.dto.AprobacionPagoDto;
import ec.edu.espe.ms_pagos.dto.PagoDto;
import ec.edu.espe.ms_pagos.dto.RespuestaPagoDto;
import ec.edu.espe.ms_pagos.dto.SolicitudPagoDto;
import ec.edu.espe.ms_pagos.service.PagoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@Slf4j
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/solicitar")
    public ResponseEntity<RespuestaPagoDto> crearSolicitudPago(@RequestBody SolicitudPagoDto solicitudPago) {
        try {
            log.info("Solicitud de pago por transferencia recibida - Asistente: {} - Evento: {}", 
                    solicitudPago.getIdAsistente(), solicitudPago.getIdEvento());
            
            RespuestaPagoDto respuesta = pagoService.crearSolicitudPago(solicitudPago);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            
        } catch (Exception e) {
            log.error("Error al crear solicitud de pago", e);
            RespuestaPagoDto respuestaError = new RespuestaPagoDto();
            respuestaError.setExitoso(false);
            respuestaError.setMensaje("Error interno del servidor: " + e.getMessage());
            respuestaError.setEstadoPago("ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PostMapping("/aprobar")
    public ResponseEntity<RespuestaPagoDto> procesarAprobacion(@RequestBody AprobacionPagoDto aprobacion) {
        try {
            log.info("Solicitud de aprobación/rechazo recibida - Pago ID: {} - Acción: {}", 
                    aprobacion.getIdPago(), aprobacion.getAccion());
            
            RespuestaPagoDto respuesta = pagoService.procesarAprobacion(aprobacion);
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            log.error("Error al procesar aprobación", e);
            RespuestaPagoDto respuestaError = new RespuestaPagoDto();
            respuestaError.setExitoso(false);
            respuestaError.setMensaje("Error interno del servidor: " + e.getMessage());
            respuestaError.setEstadoPago("ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/organizador/{idOrganizador}/pendientes")
    public ResponseEntity<List<PagoDto>> obtenerPagosPendientesPorOrganizador(@PathVariable Long idOrganizador) {
        try {
            List<PagoDto> pagos = pagoService.obtenerPagosPendientesPorOrganizador(idOrganizador);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            log.error("Error al obtener pagos pendientes por organizador: {}", idOrganizador, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/asistente/{idAsistente}")
    public ResponseEntity<List<PagoDto>> obtenerPagosPorAsistente(@PathVariable Long idAsistente) {
        try {
            List<PagoDto> pagos = pagoService.obtenerPagosPorAsistente(idAsistente);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            log.error("Error al obtener pagos por asistente: {}", idAsistente, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<List<PagoDto>> obtenerPagosPorEvento(@PathVariable Long idEvento) {
        try {
            List<PagoDto> pagos = pagoService.obtenerPagosPorEvento(idEvento);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            log.error("Error al obtener pagos por evento: {}", idEvento, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
