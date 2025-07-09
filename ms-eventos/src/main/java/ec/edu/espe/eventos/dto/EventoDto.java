package ec.edu.espe.eventos.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoDto {
    private String titulo;
    private String descripcion;
    private java.sql.Date fecha;
    private int aforo;
    private Double precioEntrada;
    private String estado;
    private String categoria;
    private String direccion;
    private String ciudad;
    private String lugar;
    private LocalDate createdAt;
    private Long idOrganizador;
}
