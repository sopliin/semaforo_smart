package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.EstadoActualSemaforo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoActualSemaforoRepository extends JpaRepository<EstadoActualSemaforo, Long> {
}
