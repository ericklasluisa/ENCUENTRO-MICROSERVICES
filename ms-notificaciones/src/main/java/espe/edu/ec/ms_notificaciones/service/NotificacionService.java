package espe.edu.ec.ms_notificaciones.service;

import espe.edu.ec.ms_notificaciones.dto.NotificacionDto;
import espe.edu.ec.ms_notificaciones.entity.Notificacion;
import espe.edu.ec.ms_notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // No olvides esto


import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {
    @Autowired
    private NotificacionRepository notificacionRepository;



    @Transactional
    public void guardarNotificacion(NotificacionDto dto){
        Notificacion notificacion = new Notificacion();


        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());
        notificacion.setFecha(LocalDateTime.now()); // La fecha la establece el consumidor

        notificacionRepository.save(notificacion);

    }

    public List<Notificacion> listarTodas(){
        return notificacionRepository.findAll();
    }


}
