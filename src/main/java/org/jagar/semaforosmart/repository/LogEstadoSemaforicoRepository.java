package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.LogEstadoSemaforico;
import org.jagar.semaforosmart.entity.Sitio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEstadoSemaforicoRepository extends JpaRepository<LogEstadoSemaforico,Integer> {
    List<LogEstadoSemaforico> findBySitio(Sitio sitio);
}
