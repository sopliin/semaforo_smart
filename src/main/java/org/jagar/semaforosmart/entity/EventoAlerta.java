package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_alertas")
@Getter
@Setter
public class EventoAlerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, length = 60)
    private String subtipo;

    @Column(nullable = false, length = 10)
    private String severidad;

    @Column(nullable = false, length = 20)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_interseccion", nullable = false)
    private Interseccion interseccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nodo")
    private Nodo nodo;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "valor_metrica", length = 60)
    private String valorMetrica;

    @Column(name = "ts_inicio", nullable = false)
    private LocalDateTime tsInicio;

    @Column(name = "ts_fin")
    private LocalDateTime tsFin;

    @Column(name = "observacion_operador", columnDefinition = "TEXT")
    private String observacionOperador;

    @Column(name = "creado_el")
    private LocalDateTime creadoEl;

    @Column(name = "actualizado_el")
    private LocalDateTime actualizadoEl;
}
