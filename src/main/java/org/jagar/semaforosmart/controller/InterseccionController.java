package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.Deteccion;
import org.jagar.semaforosmart.entity.EventoAlerta;
import org.jagar.semaforosmart.entity.Interseccion;
import org.jagar.semaforosmart.entity.Nodo;
import org.jagar.semaforosmart.repository.DeteccionRepository;
import org.jagar.semaforosmart.repository.EventoAlertaRepository;
import org.jagar.semaforosmart.repository.InterseccionRepository;
import org.jagar.semaforosmart.repository.NodoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InterseccionController {

    private final InterseccionRepository interseccionRepository;
    private final NodoRepository nodoRepository;
    private final DeteccionRepository deteccionRepository;
    private final EventoAlertaRepository eventoAlertaRepository;

    public InterseccionController(InterseccionRepository interseccionRepository,
                                  NodoRepository nodoRepository,
                                  DeteccionRepository deteccionRepository,
                                  EventoAlertaRepository eventoAlertaRepository) {
        this.interseccionRepository = interseccionRepository;
        this.nodoRepository = nodoRepository;
        this.deteccionRepository = deteccionRepository;
        this.eventoAlertaRepository = eventoAlertaRepository;
    }

    @GetMapping("/admin/intersecciones")
    public String listar(Model model) {
        List<Interseccion> intersecciones = interseccionRepository.findAllByOrderByNombreAsc();

        Map<Long, Long> nodosOjoPorInterseccion = new HashMap<>();
        Map<Long, Long> nodosCerebroPorInterseccion = new HashMap<>();
        for (Interseccion interseccion : intersecciones) {
            nodosOjoPorInterseccion.put(interseccion.getId(),
                    nodoRepository.countByInterseccion_IdAndTipo(interseccion.getId(), "ojo"));
            nodosCerebroPorInterseccion.put(interseccion.getId(),
                    nodoRepository.countByInterseccion_IdAndTipo(interseccion.getId(), "cerebro"));
        }

        model.addAttribute("intersecciones", intersecciones);
        model.addAttribute("nodosOjoPorInterseccion", nodosOjoPorInterseccion);
        model.addAttribute("nodosCerebroPorInterseccion", nodosCerebroPorInterseccion);
        return "admin/intersecciones";
    }

    @PostMapping("/admin/intersecciones")
    public String crear(@RequestParam String nombre,
                        @RequestParam String callePrincipal,
                        @RequestParam String calleSecundaria,
                        @RequestParam(required = false) String zonaDistrito,
                        RedirectAttributes redirectAttributes) {
        Interseccion interseccion = new Interseccion();
        interseccion.setNombre(nombre.trim());
        interseccion.setCallePrincipal(callePrincipal.trim());
        interseccion.setCalleSecundaria(calleSecundaria.trim());
        interseccion.setZonaDistrito((zonaDistrito == null || zonaDistrito.isBlank()) ? null : zonaDistrito.trim());
        interseccion.setEstado("operativo");
        LocalDateTime ahora = LocalDateTime.now();
        interseccion.setUltimaActualizacion(ahora);
        interseccion.setCreadoEl(ahora);
        interseccion.setActualizadoEl(ahora);
        interseccionRepository.save(interseccion);

        redirectAttributes.addFlashAttribute("mensaje", "Intersección registrada correctamente.");
        return "redirect:/admin/intersecciones";
    }

    @GetMapping("/admin/intersecciones/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Interseccion interseccion = interseccionRepository.findById(id).orElse(null);
        if (interseccion == null) {
            return "redirect:/admin/intersecciones";
        }

        List<Nodo> nodos = nodoRepository.findByInterseccion_IdOrderByTipoAscNombreAsc(id);
        List<Deteccion> detecciones = deteccionRepository.findByInterseccion_IdOrderByTsDeteccionDesc(id);
        List<EventoAlerta> alertas = eventoAlertaRepository.findByInterseccion_IdOrderByTsInicioDesc(id);

        long totalVehiculos = sumarConteo(detecciones, "vehiculo");
        long totalPeatones = sumarConteo(detecciones, "peaton");

        model.addAttribute("interseccion", interseccion);
        model.addAttribute("nodos", nodos);
        model.addAttribute("detecciones", detecciones.size() > 15 ? detecciones.subList(0, 15) : detecciones);
        model.addAttribute("alertas", alertas);
        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("totalPeatones", totalPeatones);
        return "admin/interseccionDetalle";
    }

    private long sumarConteo(List<Deteccion> detecciones, String clase) {
        return detecciones.stream()
                .filter(d -> clase.equals(d.getClase()))
                .mapToLong(d -> d.getConteoIntervalo() == null ? 0 : d.getConteoIntervalo())
                .sum();
    }
}
