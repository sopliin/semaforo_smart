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

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 20)
    private String modooperacion;

    @Column(name = "isactive")
    private boolean active;

    @Column(name = "createdat")
    private LocalDateTime createdat;
}