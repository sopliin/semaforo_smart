package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "configuracionessemaforicas")
@Getter
@Setter
public class ConfiguracionSemaforica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    private String nombre;
    private Integer segverde;
    private Integer segambar;
    private Integer segrojo;
    private Integer segciclo;
    private Double speedlimitkmh;
    private boolean isactive;
}
