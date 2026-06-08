package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.EventoAlerta;
import org.jagar.semaforosmart.repository.EventoAlertaRepository;
import org.jagar.semaforosmart.repository.InterseccionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
public class AlertaController {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "pendiente", "en_revision", "notificada", "resuelta", "descartada");

    private final EventoAlertaRepository eventoAlertaRepository;
    private final InterseccionRepository interseccionRepository;

    public AlertaController(EventoAlertaRepository eventoAlertaRepository, InterseccionRepository interseccionRepository) {
        this.eventoAlertaRepository = eventoAlertaRepository;
        this.interseccionRepository = interseccionRepository;
    }

    @GetMapping("/admin/alertas")
    public String listar(@RequestParam(required = false) String estado,
                         @RequestParam(required = false) String tipo,
                         @RequestParam(required = false) Long interseccionId,
                         Model model) {
        List<EventoAlerta> base = interseccionId != null
                ? eventoAlertaRepository.findByInterseccion_IdOrderByTsInicioDesc(interseccionId)
                : eventoAlertaRepository.findAllByOrderByTsInicioDesc();

        List<EventoAlerta> filtradas = base.stream()
                .filter(a -> estado == null || estado.isBlank() || estado.equalsIgnoreCase(a.getEstado()))
                .filter(a -> tipo == null || tipo.isBlank() || tipo.equalsIgnoreCase(a.getTipo()))
                .toList();

        model.addAttribute("alertas", filtradas);
        model.addAttribute("intersecciones", interseccionRepository.findAllByOrderByNombreAsc());
        model.addAttribute("estado", estado);
        model.addAttribute("tipo", tipo);
        model.addAttribute("interseccionId", interseccionId);
        model.addAttribute("estadosValidos", ESTADOS_VALIDOS);
        return "admin/alertas";
    }

    @PostMapping("/admin/alertas/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam String nuevoEstado,
                                @RequestParam(required = false) String observacion,
                                RedirectAttributes redirectAttributes) {
        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            redirectAttributes.addFlashAttribute("mensajeError", "Estado no reconocido.");
            return "redirect:/admin/alertas";
        }

        eventoAlertaRepository.findById(id).ifPresent(alerta -> {
            alerta.setEstado(nuevoEstado);
            if (observacion != null && !observacion.isBlank()) {
                alerta.setObservacionOperador(observacion.trim());
            }
            if ("resuelta".equals(nuevoEstado) || "descartada".equals(nuevoEstado)) {
                alerta.setTsFin(LocalDateTime.now());
            }
            alerta.setActualizadoEl(LocalDateTime.now());
            eventoAlertaRepository.save(alerta);
        });

        redirectAttributes.addFlashAttribute("mensaje", "Estado de la alerta actualizado.");
        return "redirect:/admin/alertas";
    }
}
