package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.Deteccion;
import org.jagar.semaforosmart.repository.DeteccionRepository;
import org.jagar.semaforosmart.repository.InterseccionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class HistoricoController {

    private final DeteccionRepository deteccionRepository;
    private final InterseccionRepository interseccionRepository;

    public HistoricoController(DeteccionRepository deteccionRepository, InterseccionRepository interseccionRepository) {
        this.deteccionRepository = deteccionRepository;
        this.interseccionRepository = interseccionRepository;
    }

    @GetMapping("/admin/historico")
    public String listar(@RequestParam(required = false) Long interseccionId,
                         @RequestParam(required = false) String clase,
                         @RequestParam(required = false) String desde,
                         @RequestParam(required = false) String hasta,
                         Model model) {
        List<Deteccion> base = interseccionId != null
                ? deteccionRepository.findByInterseccion_IdOrderByTsDeteccionDesc(interseccionId)
                : deteccionRepository.findTop200ByOrderByTsDeteccionDesc();

        LocalDate fechaDesde = parseFecha(desde);
        LocalDate fechaHasta = parseFecha(hasta);

        List<Deteccion> filtradas = base.stream()
                .filter(d -> clase == null || clase.isBlank() || clase.equalsIgnoreCase(d.getClase()))
                .filter(d -> fechaDesde == null || (d.getTsDeteccion() != null && !d.getTsDeteccion().toLocalDate().isBefore(fechaDesde)))
                .filter(d -> fechaHasta == null || (d.getTsDeteccion() != null && !d.getTsDeteccion().toLocalDate().isAfter(fechaHasta)))
                .toList();

        long totalVehiculos = sumarConteo(filtradas, "vehiculo");
        long totalPeatones = sumarConteo(filtradas, "peaton");

        model.addAttribute("detecciones", filtradas);
        model.addAttribute("intersecciones", interseccionRepository.findAllByOrderByNombreAsc());
        model.addAttribute("interseccionId", interseccionId);
        model.addAttribute("clase", clase);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("totalPeatones", totalPeatones);
        return "admin/historico";
    }

    private long sumarConteo(List<Deteccion> detecciones, String clase) {
        return detecciones.stream()
                .filter(d -> clase.equals(d.getClase()))
                .mapToLong(d -> d.getConteoIntervalo() == null ? 0 : d.getConteoIntervalo())
                .sum();
    }

    private LocalDate parseFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
