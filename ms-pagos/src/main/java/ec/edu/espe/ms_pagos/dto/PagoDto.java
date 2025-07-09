package ec.edu.espe.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDto {
    private Long idPago;
    private Long idAsistente;
    private Long idEvento;
    private Long idOrganizador;
    private Integer cantidadBoletos;
    private BigDecimal montoTotal;
    private String estadoPago;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAprobacion;
}
