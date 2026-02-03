package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "estadoactualsemaforo")
@Getter
@Setter
public class EstadoActualSemaforo {
    @Id
    private Long semaforoid;

    @OneToOne
    @MapsId
    @JoinColumn(name = "semaforoid")
    private Semaforo semaforo;

    @Column(name = "ts")
    private LocalDateTime ts;

    @ManyToOne
    @JoinColumn(name = "configid")
    private ConfiguracionSemaforica configuracion;

    @Column(length = 20)
    private String modooperacion;

    @Column(length = 20)
    private String coloractual;

    private Short segrestante;

    private Short segtranscurrido;

    @Column(length = 255)
    private String observaciones;
}
