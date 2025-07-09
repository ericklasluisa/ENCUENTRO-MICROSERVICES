package ec.edu.espe.eventos.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.eventos.service.EventoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InfoUsuarioListener {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private ObjectMapper mapper;

    @RabbitListener(queues = "info_usuario.cola")
    public void recibirInfoUsuario(String mensajeJson) {

    }

}
