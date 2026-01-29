package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "intervaloscaida")
@Getter
@Setter
public class IntervaloCaida {
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
    private String causa;

    @Column(length = 30)
    private String canal;

    private Integer duracionseg;
}
