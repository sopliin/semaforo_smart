package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.DeteccionRT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeteccionRTRepository extends JpaRepository<DeteccionRT, Long> {
    Optional<DeteccionRT> findTopBySitio_IdOrderByIdDesc(Long sitioId);
    Optional<DeteccionRT> findTopByOrderByIdDesc();
}
