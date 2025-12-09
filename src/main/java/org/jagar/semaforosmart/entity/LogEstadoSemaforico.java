package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "logsestadossemaforicos")
@Getter
@Setter
public class LogEstadoSemaforico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    @Column(name = "ts")
    private LocalDateTime ts;

    @ManyToOne
    @JoinColumn(name = "configid")
    private ConfiguracionSemaforica configuracionSemaforica;

    private String color;
    private Short segtranscurrido;
    private Short segrestante;

    private String estado;
}
