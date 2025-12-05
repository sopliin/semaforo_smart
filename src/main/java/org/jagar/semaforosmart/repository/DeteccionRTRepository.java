package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.DeteccionRT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeteccionRTRepository extends JpaRepository<DeteccionRT, Integer> {
    Optional<DeteccionRT> findTopBySitio_IdOrderByIdDesc(Integer sitioId);
    Optional<DeteccionRT> findTopByOrderByIdDesc();
}
