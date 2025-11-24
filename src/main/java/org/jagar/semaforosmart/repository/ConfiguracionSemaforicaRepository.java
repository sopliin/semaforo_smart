package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.ConfiguracionSemaforica;
import org.jagar.semaforosmart.entity.Sitio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfiguracionSemaforicaRepository extends JpaRepository<ConfiguracionSemaforica,Integer> {
    List<ConfiguracionSemaforica> findBySitio(Sitio sitio);
}
