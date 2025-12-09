package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordhash;

    @Column(nullable = false, length = 10)
    private String rol;

    @Column(name = "isenabled")
    private boolean enabled;

    @Column(name = "createdat")
    private LocalDateTime createdat;
}