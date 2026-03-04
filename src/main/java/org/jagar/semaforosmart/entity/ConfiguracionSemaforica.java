package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracionessemaforicas")
@Getter
@Setter
public class ConfiguracionSemaforica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semaforoid")
    private Semaforo semaforo;

    private String nombre;

    private Short segverde;
    private Short segambar;
    private Short segrojo;

    private BigDecimal speedlimitkmh;

    @Column(name = "isactive")
    private boolean isActive;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @PrePersist
    private void setCreatedAt() {
        if (createdat == null) {
            createdat = LocalDateTime.now();
        }
    }
}
