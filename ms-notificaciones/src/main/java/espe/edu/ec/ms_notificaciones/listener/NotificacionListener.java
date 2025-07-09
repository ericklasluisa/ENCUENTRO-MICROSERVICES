package espe.edu.ec.ms_notificaciones.listener;

import espe.edu.ec.ms_notificaciones.dto.NotificacionDto;
import espe.edu.ec.ms_notificaciones.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificacionListener {

    @Autowired
    private NotificacionService service;

    //@Autowired
    //private ObjectMapper mapper;

    @RabbitListener(queues = "notificaciones.cola")
    public void recibirMensajes(@Payload NotificacionDto dto) {
        log.info("Mensaje recibido y deserializado automáticamente a DTO: {}", dto);
        try{
            service.guardarNotificacion(dto); // Llama al servicio con NotificacionDto
            log.info("Notificación de tipo '{}' procesada y guardada correctamente.", dto.getTipo());

        } catch (Exception e){
            log.error("Error al procesar la notificación para el DTO: {}", dto, e);
        }
    }
}
