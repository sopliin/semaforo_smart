package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "intersecciones")
@Getter
@Setter
public class Interseccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "calle_principal", nullable = false, length = 120)
    private String callePrincipal;

    @Column(name = "calle_secundaria", nullable = false, length = 120)
    private String calleSecundaria;

    @Column(name = "zona_distrito", length = 120)
    private String zonaDistrito;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @Column(name = "creado_el")
    private LocalDateTime creadoEl;

    @Column(name = "actualizado_el")
    private LocalDateTime actualizadoEl;
}
