package espe.edu.ec.ms_notificaciones.repository;

import espe.edu.ec.ms_notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
