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

    @Column(name = "tiposemaforo")
    private String tiposemaforo;

    private String modo;

    private Short segverde;
    private Short segambar;
    private Short segrojo;
    private Short segpeatonal;
    private Short segciclo;

    private BigDecimal speedlimitkmh;
    private BigDecimal maxvelocidadkmh;
    private BigDecimal minvelocidadkmh;

    private BigDecimal segvinmin;
    private BigDecimal segvinmax;
    private BigDecimal segwinmin;
    private BigDecimal segwinmax;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "isactive")
    private boolean active;

    @Column(name = "sacarins")
    private boolean sacarins;
}