package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Interseccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterseccionRepository extends JpaRepository<Interseccion, Long> {
    List<Interseccion> findAllByOrderByNombreAsc();

    long countByEstado(String estado);
}
