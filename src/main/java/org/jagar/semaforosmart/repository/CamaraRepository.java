package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Camara;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamaraRepository extends JpaRepository<Camara, Long> {
    List<Camara> findBySitio_Id(Long sitioId);
}
