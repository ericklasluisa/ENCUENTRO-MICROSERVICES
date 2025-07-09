package ec.edu.espe.eventos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity(name = "Evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private int aforo;

    @Column(nullable = false)
    private Double precioEntrada;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, length = 100)
    private String direccion;

    @Column(nullable = false, length = 50)
    private String ciudad;

    @Column(nullable = false, length = 50)
    private String lugar;

    private LocalDate createdAt;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Boleto> boletos;

    // Id del Usuario que crea el evento (Almacenado en BD de ms-auth)
    @Column(name = "id_organizador", nullable = false)
    private Long idOrganizador;
}
