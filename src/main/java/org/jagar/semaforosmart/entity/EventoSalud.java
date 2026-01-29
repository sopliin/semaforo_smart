package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventossalud")
@Getter
@Setter
public class EventoSalud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semaforoid")
    private Semaforo semaforo;

    @Column(name = "tsevento")
    private LocalDateTime tsevento;

    @Column(length = 20)
    private String categoria;

    @Column(length = 30)
    private String canal;

    @Column(length = 20)
    private String estado;

    @Column(length = 20)
    private String origen;

    @Column(name = "detalle_json", columnDefinition = "json")
    private String detalleJson;

}
