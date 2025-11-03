package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "deteccionestiemporeal")
@Getter
@Setter
public class DeteccionRT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    private String ts;
    private Integer numvehiculos;
    private Integer numpeatones;
    private String coloractual;
    private Integer segrestante;
}
