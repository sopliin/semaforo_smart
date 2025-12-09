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
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    private String nombre;

    private Short segverde;
    private Short segambar;
    private Short segrojo;
//    private Short segpeatonal;
    private Short segciclo;

    private BigDecimal speedlimitkmh;

}