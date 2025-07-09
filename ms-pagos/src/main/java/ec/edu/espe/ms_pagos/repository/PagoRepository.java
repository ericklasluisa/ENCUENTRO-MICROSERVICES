package ec.edu.espe.ms_pagos.repository;

import ec.edu.espe.ms_pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar pagos por asistente
    List<Pago> findByIdAsistente(Long idAsistente);

    // Buscar pagos por evento
    List<Pago> findByIdEvento(Long idEvento);

    // Buscar pagos por estado
    List<Pago> findByEstadoPago(String estadoPago);

    // Buscar pagos por asistente y estado
    List<Pago> findByIdAsistenteAndEstadoPago(Long idAsistente, String estadoPago);

    // Buscar pagos por organizador y estado (para aprobar/rechazar)
    List<Pago> findByIdOrganizadorAndEstadoPago(Long idOrganizador, String estadoPago);

    // Buscar todos los pagos de un organizador
    List<Pago> findByIdOrganizador(Long idOrganizador);

    // Buscar pagos en un rango de fechas
    @Query("SELECT p FROM Pago p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Pago> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                          @Param("fechaFin") LocalDateTime fechaFin);

    // Buscar pagos aprobados por evento
    @Query("SELECT p FROM Pago p WHERE p.idEvento = :idEvento AND p.estadoPago = 'APROBADO'")
    List<Pago> findPagosAprobadosPorEvento(@Param("idEvento") Long idEvento);

    // Contar pagos por estado
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estadoPago = :estadoPago")
    long countByEstadoPago(@Param("estadoPago") String estadoPago);
}
