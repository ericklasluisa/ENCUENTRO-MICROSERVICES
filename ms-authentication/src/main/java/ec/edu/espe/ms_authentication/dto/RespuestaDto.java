package ec.edu.espe.ms_authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaDto {
    private String mensaje;
    private String token;
    private String refreshToken;
    private String usuario;
    private String rol; // Puede ser "ASISTENTE" o "ORGANIZADOR".
    private Long idUsuario;
}
