package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ordenesdecontrol")
@Getter
@Setter
public class OrdenControl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    @ManyToOne
    @JoinColumn(name = "requestedby")
    private Usuario usuario;

    private String requestedat;
    private String tipo;
    private String parametros;
    private String estado;
    private String executedat;
}
