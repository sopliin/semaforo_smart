package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.EventoSalud;
import org.jagar.semaforosmart.entity.SaludActual;
import org.jagar.semaforosmart.entity.Semaforo;
import org.jagar.semaforosmart.entity.Sitio;
import org.jagar.semaforosmart.repository.EstadoSaludRepository;
import org.jagar.semaforosmart.repository.SaludActualRepository;
import org.jagar.semaforosmart.repository.SemaforoRepository;
import org.jagar.semaforosmart.repository.SitioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class SaludSemaforoController {

    private final SitioRepository sitioRepository;
    private final SemaforoRepository semaforoRepository;
    private final EstadoSaludRepository estadoSaludRepository;
    private final SaludActualRepository saludActualRepository;

    public SaludSemaforoController(SitioRepository sitioRepository,
                                   SemaforoRepository semaforoRepository,
                                   EstadoSaludRepository estadoSaludRepository,
                                   SaludActualRepository saludActualRepository) {
        this.sitioRepository = sitioRepository;
        this.semaforoRepository = semaforoRepository;
        this.estadoSaludRepository = estadoSaludRepository;
        this.saludActualRepository = saludActualRepository;
    }

    @GetMapping("/salud")
    public String saludSemaforica(@RequestParam(required = false) Long sitioId,
                                  @RequestParam(required = false) Long semaforoId,
                                  Model model) {
        List<Sitio> sitios = sitioRepository.findAll();
        List<Semaforo> semaforos = sitioId != null
                ? semaforoRepository.findBySitio_Id(sitioId)
                : semaforoRepository.findAll();

        Long semaforoFiltro = resolveSemaforoFiltro(sitioId, semaforoId, semaforos);

        List<EventoSalud> eventos = cargarEventos(sitioId, semaforoFiltro);
        List<SaludActual> saludActuales = cargarSaludActual(sitioId, semaforoFiltro);

        long dispositivosOk = saludActuales.stream()
                .filter(sa -> sa.isJetsonOnline() && sa.isEsp32Online())
                .count();
        long dispositivosConFalla = saludActuales.size() - dispositivosOk;
        LocalDateTime ultimoEvento = eventos.stream()
                .map(EventoSalud::getTsevento)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        model.addAttribute("sitios", sitios);
        model.addAttribute("semaforos", semaforos);
        model.addAttribute("eventos", eventos);
        model.addAttribute("saludActuales", saludActuales);
        model.addAttribute("sitioId", sitioId);
        model.addAttribute("semaforoId", semaforoFiltro);
        model.addAttribute("totalEventos", eventos.size());
        model.addAttribute("dispositivosOk", dispositivosOk);
        model.addAttribute("dispositivosConFalla", dispositivosConFalla);
        model.addAttribute("ultimoEvento", ultimoEvento);
        return "admin/saludSemaforica";
    }

    private Long resolveSemaforoFiltro(Long sitioId, Long semaforoId, List<Semaforo> semaforos) {
        if (semaforoId == null) {
            return null;
        }
        Optional<Semaforo> semaforo = semaforos.stream()
                .filter(s -> Objects.equals(s.getId(), semaforoId))
                .findFirst();
        if (semaforo.isEmpty()) {
            return null;
        }
        if (sitioId == null) {
            return semaforoId;
        }
        Sitio sitio = semaforo.get().getSitio();
        return sitio != null && Objects.equals(sitio.getId(), sitioId) ? semaforoId : null;
    }

    private List<EventoSalud> cargarEventos(Long sitioId, Long semaforoId) {
        if (semaforoId != null) {
            return estadoSaludRepository.findBySemaforo_IdOrderByTseventoDesc(semaforoId);
        }
        if (sitioId != null) {
            return estadoSaludRepository.findBySemaforo_Sitio_IdOrderByTseventoDesc(sitioId);
        }
        return estadoSaludRepository.findAllByOrderByTseventoDesc();
    }

    private List<SaludActual> cargarSaludActual(Long sitioId, Long semaforoId) {
        if (semaforoId != null) {
            return saludActualRepository.findBySemaforo_Id(semaforoId)
                    .map(List::of)
                    .orElse(Collections.emptyList());
        }
        if (sitioId != null) {
            return saludActualRepository.findBySemaforo_Sitio_Id(sitioId);
        }
        return saludActualRepository.findAll();
    }
}
