package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Infraccion;
import org.jagar.semaforosmart.entity.Sitio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InfraccionRepository extends JpaRepository<Infraccion,Integer> {
    List<Infraccion> findBySitio(Sitio sitio);
}