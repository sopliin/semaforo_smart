package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sitios")
@Getter
@Setter
public class Sitio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String nombrecamara;
    private String rtspurl;
    private String modooperacion;
    private boolean isactive;
    private String createdat;
}