package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "infracciones")
@Getter
@Setter
public class Infraccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    private String ts;
    private String tipo;
    private String lightstate;
    private Double velocidadkmh;
    private String placa;
    private String fotoprocesada;
    private String createdat;
}
