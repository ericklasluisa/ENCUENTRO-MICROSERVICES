package ec.edu.espe.ms_pagos.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Cola para recibir solicitudes de pago desde ms-eventos
    @Bean
    public Queue solicitudPagosCola() {
        return QueueBuilder.durable("solicitud_pagos.cola").build();
    }

    // Cola para enviar respuestas de pago a ms-eventos
    @Bean
    public Queue respuestaPagosCola() {
        return QueueBuilder.durable("respuesta_pagos.cola").build();
    }

    // Cola para enviar notificaciones a ms-notificaciones
    @Bean
    public Queue notificacionesCola() {
        return QueueBuilder.durable("notificaciones.cola").build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
