package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.ConfiguracionSemaforica;
import org.jagar.semaforosmart.entity.Sitio;
import org.jagar.semaforosmart.model.SemaforoDetalle;
import org.jagar.semaforosmart.model.SemaforoTipo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class SemaforoDetalleRepository {

    private final ConfiguracionSemaforicaRepository configuracionRepository;
    private final SitioRepository sitioRepository;

    public SemaforoDetalleRepository(ConfiguracionSemaforicaRepository configuracionRepository,
                                     SitioRepository sitioRepository) {
        this.configuracionRepository = configuracionRepository;
        this.sitioRepository = sitioRepository;
    }

    public List<SemaforoDetalle> findAll(Long sitioId) {
        List<ConfiguracionSemaforica> configuraciones = sitioId == null
                ? configuracionRepository.findAll()
                : sitioRepository.findById(sitioId)
                .map(configuracionRepository::findBySitio)
                .orElse(List.of());

        return configuraciones.stream()
                .map(this::mapear)
                .sorted(Comparator.comparingLong(SemaforoDetalle::getId))
                .toList();
    }

    public Optional<SemaforoDetalle> findById(Long id) {
        return  configuracionRepository.findById(id).map(this::mapear);
    }

    public Optional<SemaforoDetalle> updateTiempos(Integer id, int rojo, int amarillo, int verde, int peatonal, boolean modoPrioridad) {
        return configuracionRepository.findById(Long.valueOf(id)).map(cfg -> {
            cfg.setSegrojo((short) rojo);
            cfg.setSegambar((short) amarillo);
            cfg.setSegverde((short) verde);
            cfg.setSegciclo((short) (rojo + amarillo + verde + peatonal));
            Sitio sitio = cfg.getSitio();
            if (sitio != null) {
                sitio.setModooperacion(modoPrioridad ? "PRIORITY" : "DEFAULT");
                sitioRepository.save(sitio);
            }
            ConfiguracionSemaforica guardado = configuracionRepository.save(cfg);
            return mapear(guardado);
        });
    }

    public Optional<SemaforoDetalle> crearConfiguracion(Long sitioId, String nombre, int rojo, int amarillo, int verde,
                                                        java.math.BigDecimal velocidadLimiteKmh) {
        Sitio sitio = sitioId != null
                ? sitioRepository.findById(sitioId).orElse(null)
                : sitioRepository.findTopByOrderByIdDesc().orElse(null);

        if (sitio == null) {
            return Optional.empty();
        }

        ConfiguracionSemaforica cfg = new ConfiguracionSemaforica();
        cfg.setSitio(sitio);
        cfg.setNombre((nombre == null || nombre.isBlank()) ? null : nombre.trim());
        cfg.setSegrojo((short) rojo);
        cfg.setSegambar((short) amarillo);
        cfg.setSegverde((short) verde);
        cfg.setIsactive(true);
        if (velocidadLimiteKmh != null) {
            cfg.setSpeedlimitkmh(velocidadLimiteKmh);
        } else {
            cfg.setSpeedlimitkmh(null);
        }

        ConfiguracionSemaforica guardado = configuracionRepository.save(cfg);
        return Optional.of(mapear(guardado));
    }

    private SemaforoDetalle mapear(ConfiguracionSemaforica configuracion){
        Sitio sitio = configuracion.getSitio();
        SemaforoDetalle detalle = new SemaforoDetalle();
        detalle.setId(configuracion.getId());
        String nombreCfg = configuracion.getNombre();
        if (nombreCfg == null || nombreCfg.isBlank()) {
            nombreCfg = "Configuración semafórica";
        }
        detalle.setNombre(nombreCfg);

//        detalle.setDescripcion(Optional.ofNullable(configuracion.getNombre()).orElse("Configuración Semafórica"));
//        detalle.setUbicacion(sitio != null ? "Configuracion asociada al sitio "+ sitio.getNombre() : "Configuración Semafórica");
        detalle.setTipo(deducirTipo(configuracion.getNombre()));
        detalle.setTiempoRojo(Optional.ofNullable(configuracion.getSegrojo()).orElse((short) 0));
        detalle.setTiempoAmarillo(Optional.ofNullable(configuracion.getSegambar()).orElse((short) 0));
        detalle.setTiempoVerde(Optional.ofNullable(configuracion.getSegverde()).orElse((short) 0));
        detalle.setVelocidadLimiteKmh(configuracion.getSpeedlimitkmh());
        return detalle;
    }

    private SemaforoTipo deducirTipo(String tipo){
        if (tipo == null) {
            return SemaforoTipo.VEHICULAR;
        }
        String lower = tipo.toLowerCase(Locale.ROOT);
        if (lower.contains("peaton")){
            return SemaforoTipo.PEATONAL;
        }
        return SemaforoTipo.VEHICULAR;
    }
}
