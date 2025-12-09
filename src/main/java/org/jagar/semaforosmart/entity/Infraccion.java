package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "infracciones")
@Getter
@Setter
public class Infraccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    @Column(name = "ts")
    private LocalDateTime ts;

    private String tipo;

    private String lightstate;

    private BigDecimal velocidadkmh;

    private String placa;

    @Column(name = "fotoprocesada")
    private String fotoprocesada;

    @Column(name = "createdat")
    private LocalDateTime createdat;
}
