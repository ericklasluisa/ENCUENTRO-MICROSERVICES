package ec.edu.espe.eventos.repository;

import ec.edu.espe.eventos.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Integer countByEventoIdEvento(Long idEvento);

    @Query("SELECT DISTINCT b.idAsistente FROM Boleto b WHERE b.evento.idEvento = :idEvento")
    List<Long> findIdsAsistentesByEventoIdEvento(@Param("idEvento") Long idEvento);

}
