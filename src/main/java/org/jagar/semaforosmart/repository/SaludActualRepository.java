package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.SaludActual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaludActualRepository extends JpaRepository<SaludActual, Long> {
    List<SaludActual> findBySemaforo_Sitio_Id(Long sitioId);

    Optional<SaludActual> findBySemaforo_Id(Long semaforoId);
}
