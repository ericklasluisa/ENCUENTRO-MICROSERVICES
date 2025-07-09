package ec.edu.espe.ms_pagos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(name = "id_asistente", nullable = false)
    private Long idAsistente;

    @Column(name = "id_evento", nullable = false)
    private Long idEvento;

    @Column(name = "cantidad_boletos", nullable = false)
    private Integer cantidadBoletos;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago; // PENDIENTE, APROBADO, RECHAZADO

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "id_organizador", nullable = false)
    private Long idOrganizador; // ID del organizador que debe aprobar

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estadoPago == null) {
            estadoPago = "PENDIENTE";
        }
    }
}
