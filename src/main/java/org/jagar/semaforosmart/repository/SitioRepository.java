package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Sitio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SitioRepository extends JpaRepository<Sitio,Long> {
    Optional<Sitio> findTopByOrderByIdDesc();
}