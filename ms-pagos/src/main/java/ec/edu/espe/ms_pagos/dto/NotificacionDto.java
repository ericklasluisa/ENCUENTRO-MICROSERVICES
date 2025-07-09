package ec.edu.espe.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDto {
    private Long idUsuario;
    private String mensaje;
    private String tipo;
    private String asunto;
}
