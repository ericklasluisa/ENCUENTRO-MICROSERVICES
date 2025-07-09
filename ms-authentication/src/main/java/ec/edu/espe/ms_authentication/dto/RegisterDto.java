package ec.edu.espe.ms_authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    String nombre;
    String apellido;
    String correo;
    String contrasena;
    Date fechaNacimiento;
    String rol; // Puede ser "ASISTENTE" o "ORGANIZADOR".

    // Campos opcionales para rol ORGANIZADOR
    String empresa;
    String ruc;
}
