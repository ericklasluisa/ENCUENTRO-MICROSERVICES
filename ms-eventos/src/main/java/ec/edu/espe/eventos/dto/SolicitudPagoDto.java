package ec.edu.espe.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudPagoDto {
    private Long idAsistente;
    private Long idEvento;
    private Long idOrganizador;
    private Integer cantidadBoletos;
    private BigDecimal montoTotal;
}
