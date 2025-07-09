package ec.edu.espe.ms_authentication.service;

import ec.edu.espe.ms_authentication.dto.NotificacionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificacionProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarNotificacionRegistro(String nombreUsuario, String rolUsuario) {
        try {
            NotificacionDto notificacion = new NotificacionDto();
            notificacion.setMensaje(String.format("Bienvenido %s! Te has registrado exitosamente como %s en la plataforma de eventos.", 
                                                 nombreUsuario, rolUsuario));
            notificacion.setTipo("REGISTRO_USUARIO");

            rabbitTemplate.convertAndSend("notificaciones.cola", notificacion);
            
            log.info("Notificación de registro enviada para usuario: {} con rol: {}", nombreUsuario, rolUsuario);
        } catch (Exception e) {
            log.error("Error al enviar notificación de registro para usuario: {}", nombreUsuario, e);
        }
    }
}
