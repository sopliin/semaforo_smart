package org.jagar.semaforosmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nodos")
@Getter
@Setter
public class Nodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_interseccion", nullable = false)
    private Interseccion interseccion;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 45)
    private String ip;

    @Column(name = "estado_conexion", nullable = false, length = 20)
    private String estadoConexion;

    @Column(name = "temperatura_c", precision = 5, scale = 2)
    private BigDecimal temperaturaC;

    @Column(name = "cpu_pct", precision = 5, scale = 2)
    private BigDecimal cpuPct;

    @Column(name = "ram_pct", precision = 5, scale = 2)
    private BigDecimal ramPct;

    @Column(name = "almacenamiento_libre_mb")
    private Integer almacenamientoLibreMb;

    @Column(name = "camara_estado", length = 20)
    private String camaraEstado;

    @Column(precision = 5, scale = 2)
    private BigDecimal fps;

    @Column(name = "latencia_ms")
    private Integer latenciaMs;

    @Column(name = "version_software", length = 40)
    private String versionSoftware;

    @Column(name = "reinicios_recientes", nullable = false)
    private Integer reiniciosRecientes;

    @Column(name = "ultimo_heartbeat")
    private LocalDateTime ultimoHeartbeat;

    @Column(name = "creado_el")
    private LocalDateTime creadoEl;

    @Column(name = "actualizado_el")
    private LocalDateTime actualizadoEl;
}
