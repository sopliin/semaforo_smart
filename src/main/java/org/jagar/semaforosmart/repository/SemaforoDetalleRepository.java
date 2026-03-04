package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.ConfiguracionSemaforica;
import org.jagar.semaforosmart.entity.Semaforo;
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
    private final SemaforoRepository semaforoRepository;

    public SemaforoDetalleRepository(ConfiguracionSemaforicaRepository configuracionRepository,
                                     SemaforoRepository semaforoRepository) {
        this.configuracionRepository = configuracionRepository;
        this.semaforoRepository = semaforoRepository;
    }

    public List<SemaforoDetalle> findAll(Long sitioId) {
        List<ConfiguracionSemaforica> configuraciones = sitioId == null
                ? configuracionRepository.findAll()
                : configuracionRepository.findBySemaforo_Sitio_Id(sitioId);

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
            Semaforo semaforo = cfg.getSemaforo();
            if (semaforo != null) {
                semaforo.setModooperacion(modoPrioridad ? "PRIORITY" : "DEFAULT");
                semaforoRepository.save(semaforo);
            }
            ConfiguracionSemaforica guardado = configuracionRepository.save(cfg);
            return mapear(guardado);
        });
    }

    public Optional<SemaforoDetalle> crearConfiguracion(Long sitioId, String nombre, int rojo, int amarillo, int verde,
                                                        java.math.BigDecimal velocidadLimiteKmh) {
        Semaforo semaforo = resolveSemaforo(sitioId);

        if (semaforo == null) {
            return Optional.empty();
        }

        ConfiguracionSemaforica cfg = new ConfiguracionSemaforica();
        cfg.setSemaforo(semaforo);
        cfg.setNombre((nombre == null || nombre.isBlank()) ? null : nombre.trim());
        cfg.setSegrojo((short) rojo);
        cfg.setSegambar((short) amarillo);
        cfg.setSegverde((short) verde);
        cfg.setActive(true);
        if (velocidadLimiteKmh != null) {
            cfg.setSpeedlimitkmh(velocidadLimiteKmh);
        } else {
            cfg.setSpeedlimitkmh(null);
        }

        ConfiguracionSemaforica guardado = configuracionRepository.save(cfg);
        return Optional.of(mapear(guardado));
    }

    private SemaforoDetalle mapear(ConfiguracionSemaforica configuracion){
        Semaforo semaforo = configuracion.getSemaforo();
        SemaforoDetalle detalle = new SemaforoDetalle();
        detalle.setId(configuracion.getId());
        String nombreCfg = configuracion.getNombre();
        if (nombreCfg == null || nombreCfg.isBlank()) {
            nombreCfg = "Configuración semafórica";
        }
        detalle.setNombre(nombreCfg);

        detalle.setTipo(deducirTipo(semaforo != null ? semaforo.getTipo() : configuracion.getNombre()));
        detalle.setTiempoRojo(Optional.ofNullable(configuracion.getSegrojo()).orElse((short) 0));
        detalle.setTiempoAmarillo(Optional.ofNullable(configuracion.getSegambar()).orElse((short) 0));
        detalle.setTiempoVerde(Optional.ofNullable(configuracion.getSegverde()).orElse((short) 0));
        detalle.setVelocidadLimiteKmh(configuracion.getSpeedlimitkmh());
        return detalle;
    }

    private Semaforo resolveSemaforo(Long sitioId) {
        List<Semaforo> semaforos = sitioId == null
                ? semaforoRepository.findAll()
                : semaforoRepository.findBySitio_Id(sitioId);

        return semaforos.stream()
                .filter(this::esVehicular)
                .findFirst()
                .orElseGet(() -> semaforos.stream().findFirst().orElse(null));
    }

    private boolean esVehicular(Semaforo semaforo) {
        if (semaforo == null) {
            return false;
        }
        String tipo = semaforo.getTipo();
        if (tipo == null || tipo.isBlank()) {
            return true;
        }
        return !tipo.toLowerCase(Locale.ROOT).contains("peaton");
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
