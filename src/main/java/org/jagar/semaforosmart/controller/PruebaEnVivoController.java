package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.Interseccion;
import org.jagar.semaforosmart.repository.InterseccionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PruebaEnVivoController {

    private final InterseccionRepository interseccionRepository;

    @Value("${app.prueba-en-vivo.stream-url:}")
    private String streamUrl;

    public PruebaEnVivoController(InterseccionRepository interseccionRepository) {
        this.interseccionRepository = interseccionRepository;
    }

    @GetMapping("/admin/prueba-en-vivo")
    public String pruebaEnVivo(@RequestParam(required = false) Long interseccionId, Model model) {
        List<Interseccion> intersecciones = interseccionRepository.findAllByOrderByNombreAsc();
        Interseccion seleccionada = null;
        if (!intersecciones.isEmpty()) {
            seleccionada = intersecciones.stream()
                    .filter(i -> i.getId().equals(interseccionId))
                    .findFirst()
                    .orElse(intersecciones.get(0));
        }

        model.addAttribute("intersecciones", intersecciones);
        model.addAttribute("seleccionada", seleccionada);
        model.addAttribute("streamUrl", (streamUrl == null || streamUrl.isBlank()) ? null : streamUrl.trim());
        return "admin/pruebaEnVivo";
    }
}
