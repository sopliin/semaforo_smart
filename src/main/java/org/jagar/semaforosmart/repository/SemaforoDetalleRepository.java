package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.ConfiguracionSemaforica;
import org.jagar.semaforosmart.entity.Sitio;
import org.jagar.semaforosmart.model.SemaforoDetalle;
import org.jagar.semaforosmart.model.SemaforoTipo;
import org.springframework.stereotype.Repository;

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

    private SemaforoDetalle mapear(ConfiguracionSemaforica configuracion){
        Sitio sitio = configuracion.getSitio();
        SemaforoDetalle detalle = new SemaforoDetalle();
        detalle.setId(configuracion.getId());
        detalle.setNombre(configuracion.getNombre());
        detalle.setUbicacion(sitio != null ? "Configuracion asociada al sitio "+ sitio.getNombre() : "Configuración Semafórica");
        detalle.setTiempoRojo(Optional.ofNullable(configuracion.getSegrojo()).orElse((short) 0));
        detalle.setTiempoAmarillo(Optional.ofNullable(configuracion.getSegambar()).orElse((short) 0));
        detalle.setTiempoVerde(Optional.ofNullable(configuracion.getSegverde()).orElse((short) 0));
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
