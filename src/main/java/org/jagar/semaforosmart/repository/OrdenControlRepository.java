package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.OrdenControl;
import org.jagar.semaforosmart.entity.Sitio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenControlRepository extends JpaRepository<OrdenControl,Long> {
    List<OrdenControl> findBySitio(Sitio sitio);
}