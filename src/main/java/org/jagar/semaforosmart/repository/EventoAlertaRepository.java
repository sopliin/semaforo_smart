package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.EventoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoAlertaRepository extends JpaRepository<EventoAlerta, Long> {
    List<EventoAlerta> findAllByOrderByTsInicioDesc();

    List<EventoAlerta> findByInterseccion_IdOrderByTsInicioDesc(Long interseccionId);

    List<EventoAlerta> findTop6ByOrderByTsInicioDesc();

    long countByEstado(String estado);
}
