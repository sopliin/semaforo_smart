package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Deteccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeteccionRepository extends JpaRepository<Deteccion, Long> {
    List<Deteccion> findTop200ByOrderByTsDeteccionDesc();

    List<Deteccion> findByInterseccion_IdOrderByTsDeteccionDesc(Long interseccionId);
}
