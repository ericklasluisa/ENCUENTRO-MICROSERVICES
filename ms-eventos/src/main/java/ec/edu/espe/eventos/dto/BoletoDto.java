package ec.edu.espe.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletoDto {

    private Long idAsistente;

    private Long idEvento;

    private Integer cantidadBoletos;

}
