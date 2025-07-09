package ec.edu.espe.eventos.service;

import ec.edu.espe.eventos.dto.CompraBoletoDto;
import ec.edu.espe.eventos.dto.SolicitudPagoDto;
import ec.edu.espe.eventos.model.Evento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class CompraService {

    @Autowired
    @Lazy
    private EventoService eventoService;

    @Autowired
    private PagoProducer pagoProducer;

    public String procesarCompraConPago(CompraBoletoDto compraDto) {
        try {
            log.info("Procesando compra de boletos - Asistente: {} - Evento: {} - Cantidad: {}", 
                    compraDto.getIdAsistente(), compraDto.getIdEvento(), compraDto.getCantidadBoletos());

            // Validaciones básicas
            if (compraDto.getIdAsistente() == null || compraDto.getIdEvento() == null || compraDto.getCantidadBoletos() == null) {
                throw new IllegalArgumentException("Todos los campos son requeridos");
            }

            if (compraDto.getCantidadBoletos() <= 0) {
                throw new IllegalArgumentException("La cantidad de boletos debe ser mayor a 0");
            }

            // Obtener información del evento
            Evento evento = eventoService.obtenerEventoPorId(compraDto.getIdEvento());
            
            // Verificar disponibilidad
            int disponibilidad = eventoService.verificarDisponibilidad(compraDto.getIdEvento());
            if (disponibilidad < compraDto.getCantidadBoletos()) {
                throw new IllegalArgumentException("Solo hay " + disponibilidad + " boletos disponibles. Solicitados: " + compraDto.getCantidadBoletos());
            }

            // Calcular monto total
            BigDecimal montoTotal = BigDecimal.valueOf(evento.getPrecioEntrada()).multiply(BigDecimal.valueOf(compraDto.getCantidadBoletos()));

            // Crear solicitud de pago
            SolicitudPagoDto solicitudPago = new SolicitudPagoDto();
            solicitudPago.setIdAsistente(compraDto.getIdAsistente());
            solicitudPago.setIdEvento(compraDto.getIdEvento());
            solicitudPago.setIdOrganizador(evento.getIdOrganizador());
            solicitudPago.setCantidadBoletos(compraDto.getCantidadBoletos());
            solicitudPago.setMontoTotal(montoTotal);

            // Enviar solicitud de pago al microservicio de pagos
            pagoProducer.enviarSolicitudPago(solicitudPago);

            log.info("Solicitud de pago enviada exitosamente - Monto: {}", montoTotal);

            return String.format("Solicitud de compra enviada exitosamente. Monto total: $%.2f. " +
                    "El organizador debe aprobar el pago por transferencia para confirmar los boletos.", 
                    montoTotal.doubleValue());

        } catch (IllegalArgumentException e) {
            log.error("Error de validación en compra: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al procesar compra", e);
            throw new RuntimeException("Error interno al procesar la compra: " + e.getMessage(), e);
        }
    }
}
