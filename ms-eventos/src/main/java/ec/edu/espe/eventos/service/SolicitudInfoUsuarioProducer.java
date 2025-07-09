package ec.edu.espe.eventos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudInfoUsuarioProducer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ObjectMapper mapper;

    public void enviarSolicitud(String operacion) {
        try {
            template.convertAndSend("solicitud_info_usuario.cola", operacion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void solicitudListaAsistentes(List<Long> ListaIdsAsistentes) {
        try {
            String json = mapper.writeValueAsString(ListaIdsAsistentes);
            template.convertAndSend("solicitud_lista.cola", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
