package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.ConfiguracionSemaforica;
import org.jagar.semaforosmart.entity.Semaforo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfiguracionSemaforicaRepository extends JpaRepository<ConfiguracionSemaforica,Long> {
    List<ConfiguracionSemaforica> findBySemaforo(Semaforo semaforo);
    List<ConfiguracionSemaforica> findBySemaforo_Sitio_Id(Long sitioId);
}
