package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "fasessemaforo")
@Getter
@Setter
public class FaseSemaforo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semaforoid")
    private Semaforo semaforo;

    @Column(name = "tsinicio")
    private LocalDateTime tsinicio;

    @Column(name = "tsfin")
    private LocalDateTime tsfin;

    @Column(length = 20)
    private String color;

    private Integer duracionseg;

    @ManyToOne
    @JoinColumn(name = "configid")
    private ConfiguracionSemaforica configuracion;
}
