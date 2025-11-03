package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logsestadossemaforicos")
@Getter
@Setter
public class LogEstadoSemaforico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    private String ts;

    @ManyToOne
    @JoinColumn(name = "configid")
    private ConfiguracionSemaforica configuracionSemaforica;

    private String color;
    private Integer segtranscurrido;
    private Integer segrestante;
}
