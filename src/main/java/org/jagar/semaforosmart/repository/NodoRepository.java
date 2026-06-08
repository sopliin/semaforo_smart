package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Nodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodoRepository extends JpaRepository<Nodo, Long> {
    List<Nodo> findByInterseccion_IdOrderByTipoAscNombreAsc(Long interseccionId);

    List<Nodo> findAllByOrderByInterseccion_IdAscTipoAscNombreAsc();

    long countByTipo(String tipo);

    long countByTipoAndEstadoConexion(String tipo, String estadoConexion);

    long countByInterseccion_IdAndTipo(Long interseccionId, String tipo);
}
