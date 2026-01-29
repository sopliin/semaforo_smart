package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Semaforo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemaforoRepository extends JpaRepository<Semaforo, Long> {
    List<Semaforo> findBySitio_Id(Long sitioId);
}
