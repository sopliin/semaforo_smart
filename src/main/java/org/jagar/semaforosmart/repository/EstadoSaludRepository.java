package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.EventoSalud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoSaludRepository extends JpaRepository<EventoSalud, Long> {
}
