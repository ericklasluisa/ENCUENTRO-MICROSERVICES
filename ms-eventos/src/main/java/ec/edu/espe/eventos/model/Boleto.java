package ec.edu.espe.eventos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "Boleto")
public class Boleto {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long idBoleto;

    private LocalDateTime fechaCompra;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_evento", nullable = false)
    @JsonIgnore
    private Evento evento;

    // Id del Usuario que compra el boleto (Almacenado en BD de ms-auth)
    @Column(name = "id_asistente", nullable = false)
    private Long idAsistente;
}
