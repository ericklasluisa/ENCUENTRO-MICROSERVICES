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

    // Cola para enviar solicitudes de pago a ms-pagos
    @Bean
    public Queue solicitudPagosCola() {
        return QueueBuilder.durable("solicitud_pagos.cola").build();
    }

    // Cola para recibir respuestas de pago desde ms-pagos
    @Bean
    public Queue respuestaPagosCola() {
        return QueueBuilder.durable("respuesta_pagos.cola").build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
