package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.EventoSalud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstadoSaludRepository extends JpaRepository<EventoSalud, Long> {
    List<EventoSalud> findAllByOrderByTseventoDesc();

    List<EventoSalud> findBySemaforo_IdOrderByTseventoDesc(Long semaforoId);

    List<EventoSalud> findBySemaforo_Sitio_IdOrderByTseventoDesc(Long sitioId);
}
