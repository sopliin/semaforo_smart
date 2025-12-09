package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "deteccionestemporalsemaforos")
@Getter
@Setter
public class DeteccionRT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    @Column(name = "ts")
    private LocalDateTime ts;

    private Integer numvehiculos;
    private Integer numpeatones;

    private String coloractual;

    private Short segrestante;
}