package ec.edu.espe.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPagoDto {
    private boolean exitoso;
    private String mensaje;
    private String estadoPago;
    private Long idPago;
    private Long idAsistente;
    private Long idEvento;
    private Integer cantidadBoletos;
    private BigDecimal montoTotal;
}
