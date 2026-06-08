package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "detecciones")
@Getter
@Setter
public class Deteccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nodo", nullable = false)
    private Nodo nodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_interseccion", nullable = false)
    private Interseccion interseccion;

    @Column(name = "ts_deteccion")
    private LocalDateTime tsDeteccion;

    @Column(nullable = false, length = 10)
    private String clase;

    @Column(name = "conteo_intervalo", nullable = false)
    private Integer conteoIntervalo;
}
