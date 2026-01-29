package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "semaforos")
@Getter
@Setter
public class Semaforo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sitioid")
    private Sitio sitio;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 20)
    private String tipo;

    @Column(length = 20)
    private String modooperacion;

    @Column(name = "esp32_device_id", length = 80)
    private String esp32DeviceId;

    @Column(name = "jetson_device_id", length = 80)
    private String jetsonDeviceId;

    @Column(name = "isactive")
    private boolean isActive;

    @Column(name = "createdat")
    private LocalDateTime createdat;
}
