package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saludactual")
@Getter
@Setter
public class SaludActual {
    @Id
    private Long semaforoid;

    @OneToOne
    @MapsId
    @JoinColumn(name = "semaforoid")
    private Semaforo semaforo;

    @Column(name = "ts_update")
    private LocalDateTime tsUpdate;

    @Column(name = "jetson_online")
    private boolean jetsonOnline;

    @Column(name = "esp32_online")
    private boolean esp32Online;

    @Column(name = "ultimo_ok_jetson_esp32")
    private LocalDateTime ultimoOkJetsonEsp32;

    @Column(name = "ultimo_fail_jetson_esp32")
    private LocalDateTime ultimoFailJetsonEsp32;

    @Column(name = "ultimo_ok_esp32_potencia")
    private LocalDateTime ultimoOkEsp32Potencia;

    @Column(name = "ultimo_fail_esp32_potencia")
    private LocalDateTime ultimoFailEsp32Potencia;

    @Column(name = "ultimo_power_lost_jetson")
    private LocalDateTime ultimoPowerLostJetson;

    @Column(name = "ultimo_power_lost_esp32")
    private LocalDateTime ultimoPowerLostEsp32;

    @Column(name = "ultimo_cloud_ok")
    private LocalDateTime ultimoCloudOk;

    @Column(name = "ultimo_cloud_fail")
    private LocalDateTime ultimoCloudFail;

    @Column(length = 255)
    private String observaciones;
}
