package ec.edu.espe.eventos.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Cola para enviar notificaciones al microservicio de notificaciones
    @Bean
    public Queue notificacionesCola() {
        return QueueBuilder.durable("notificaciones.cola").build();
    }

    // Cola para recibir información de usuarios (se mantiene porque se usa en InfoUsuarioListener)
    @Bean
    public Queue infoUsuario() {
        return QueueBuilder.durable("info_usuario.cola").build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
