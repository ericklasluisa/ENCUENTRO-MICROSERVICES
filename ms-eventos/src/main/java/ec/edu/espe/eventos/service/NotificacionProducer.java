package ec.edu.espe.eventos.service;

import ec.edu.espe.eventos.dto.NotificacionDto;
import ec.edu.espe.eventos.model.Evento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificacionProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarNotificacionCreacionEvento(Evento evento) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setMensaje(String.format("¡Nuevo evento creado! '%s' programado para %s. Aforo disponible: %d personas.", 
                                                 evento.getTitulo(), evento.getFecha(), evento.getAforo()));
            notificacion.setTipo("CREACION_EVENTO");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de creación de evento enviada para: {}", evento.getTitulo());
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación para evento: {}", evento.getTitulo(), e);
        }
    }

    public void enviarNotificacionActualizacionEvento(Evento evento) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setMensaje(String.format("Evento actualizado: '%s'. Se han modificado los detalles del evento. Verifica la información actualizada.", 
                                                 evento.getTitulo()));
            notificacion.setTipo("ACTUALIZACION_EVENTO");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de actualización de evento enviada para: {}", evento.getTitulo());
        } catch (Exception e) {
            log.error("Error al enviar notificación de actualización para evento: {}", evento.getTitulo(), e);
        }
    }

    public void enviarNotificacionEliminacionEvento(String nombreEvento) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setMensaje(String.format("Evento cancelado: '%s' ha sido eliminado. Se notificará a todos los asistentes registrados.", 
                                                 nombreEvento));
            notificacion.setTipo("ELIMINACION_EVENTO");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de eliminación de evento enviada para: {}", nombreEvento);
        } catch (Exception e) {
            log.error("Error al enviar notificación de eliminación para evento: {}", nombreEvento, e);
        }
    }

    public void enviarNotificacionCompraBoleto(Evento evento, Long idAsistente, int cantidadBoletos) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setMensaje(String.format("¡Boletos comprados exitosamente! %d boleto(s) para el evento '%s'. ¡Te esperamos!", 
                                                 cantidadBoletos, evento.getTitulo()));
            notificacion.setTipo("COMPRA_BOLETO");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de compra de boletos enviada para evento: {} - Asistente: {} - Cantidad: {}", 
                    evento.getTitulo(), idAsistente, cantidadBoletos);
        } catch (Exception e) {
            log.error("Error al enviar notificación de compra de boletos para evento: {}", evento.getTitulo(), e);
        }
    }
}
