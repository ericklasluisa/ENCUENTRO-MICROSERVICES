package ec.edu.espe.eventos.repository;

import ec.edu.espe.eventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findAllByIdOrganizador(Long idOrganizador);
}
