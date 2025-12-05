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

    public List<SemaforoDetalle> findAll(Integer sitioId) {
        List<ConfiguracionSemaforica> configuraciones = sitioId == null
                ? configuracionRepository.findAll()
                : sitioRepository.findById(sitioId)
                .map(configuracionRepository::findBySitio)
                .orElse(List.of());

        return configuraciones.stream()
                .map(this::mapear)
                .sorted(Comparator.comparingInt(SemaforoDetalle::getId))
                .toList();
    }

    public Optional<SemaforoDetalle> findById(Integer id) {
        return  configuracionRepository.findById(id).map(this::mapear);
    }

    public Optional<SemaforoDetalle> updateTiempos(Integer id, int rojo, int amarillo, int verde, int peatonal, boolean modoAutomatico) {
        return configuracionRepository.findById(id).map(cfg -> {
            cfg.setSegrojo(rojo);
            cfg.setSegambar(amarillo);
            cfg.setSegverde(verde);
            cfg.setSegciclo(peatonal);
            Sitio sitio = cfg.getSitio();
            if (sitio != null) {
                sitio.setModooperacion(modoAutomatico ? "AUTO" : "MANUAL");
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
        detalle.setTipo(deducirTipo(configuracion.getNombre()));
        detalle.setUbicacion(sitio != null ? "Configuracion asociada al sitio "+ sitio.getNombre() : "Configuración Semafórica");
        detalle.setModoAutomatico(sitio == null || "AUTO".equals(sitio.getModooperacion()));
        detalle.setTiempoRojo(Optional.ofNullable(configuracion.getSegrojo()).orElse(0));
        detalle.setTiempoAmarillo(Optional.ofNullable(configuracion.getSegambar()).orElse(0));
        detalle.setTiempoVerde(Optional.ofNullable(configuracion.getSegverde()).orElse(0));
        detalle.setTiempoPeatonal(Optional.ofNullable(configuracion.getSegciclo()).orElse(0));
        return detalle;
    }

    private SemaforoTipo deducirTipo(String nombre){
        if (nombre == null) {
            return SemaforoTipo.VEHICULAR;
        }
        String lower = nombre.toLowerCase(Locale.ROOT);
        if (lower.contains("peaton")){
            return SemaforoTipo.PEATONAL;
        }
        return SemaforoTipo.VEHICULAR;
    }
}
