package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordenesdecontrol")
@Getter
@Setter
public class OrdenControl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semaforoid")
    private Semaforo semaforo;

    @ManyToOne
    @JoinColumn(name = "requestedby")
    private Usuario usuario;

    @Column(name = "requestedat")
    private LocalDateTime requestedat;

    private String tipo;

    @Column(name = "parametros_json", columnDefinition = "json")
    private String parametrosJson;

    private String estado;

    @Column(name = "executedat")
    private LocalDateTime executedat;

    private String resultado;
}