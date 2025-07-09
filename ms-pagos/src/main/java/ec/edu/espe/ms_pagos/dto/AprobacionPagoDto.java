package ec.edu.espe.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AprobacionPagoDto {
    private Long idPago;
    private String accion; // "APROBAR" o "RECHAZAR"
}
