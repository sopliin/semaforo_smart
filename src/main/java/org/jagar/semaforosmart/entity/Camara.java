package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "camaras")
@Getter
@Setter
public class Camara {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(name = "rtspurl", length = 512)
    private String rtspurl;

    @Column(name = "isactive")
    private boolean isactive;

    @Column(name = "createdat")
    private LocalDateTime createdat;
}
