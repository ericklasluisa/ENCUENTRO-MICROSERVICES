package ec.edu.espe.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioInfoDto {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;
}
