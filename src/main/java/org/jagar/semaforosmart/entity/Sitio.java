package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sitios")
@Getter
@Setter
public class Sitio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "modooperacion_default", length = 20)
    private String modooperacionDefault;

    @Column(name = "croquis_url", length = 512)
    private String croquisUrl;

    @Column(name = "croquis_json", columnDefinition = "json")
    private String croquisJson;

    @Column(name = "isactive")
    private boolean active;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "updatedat")
    private LocalDateTime updatedat;
}