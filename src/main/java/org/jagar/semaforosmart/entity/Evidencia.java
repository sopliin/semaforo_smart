package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidencias")
@Getter
@Setter
public class Evidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semaforoid")
    private Semaforo semaforo;

    @Column(name = "ts")
    private LocalDateTime ts;

    @Column(length = 20)
    private String tipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "storage_provider", length = 20)
    private String storageProvider;

    @Column(length = 1024)
    private String url;

    @Column(length = 120)
    private String mime;

    @Column(length = 64)
    private String sha256;

    @Column(name = "createdat")
    private LocalDateTime createdat;
}
