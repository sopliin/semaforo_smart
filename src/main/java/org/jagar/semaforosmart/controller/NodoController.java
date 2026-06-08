package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.Nodo;
import org.jagar.semaforosmart.repository.InterseccionRepository;
import org.jagar.semaforosmart.repository.NodoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NodoController {

    private final NodoRepository nodoRepository;
    private final InterseccionRepository interseccionRepository;

    public NodoController(NodoRepository nodoRepository, InterseccionRepository interseccionRepository) {
        this.nodoRepository = nodoRepository;
        this.interseccionRepository = interseccionRepository;
    }

    @GetMapping("/admin/nodos")
    public String listar(@RequestParam(required = false) Long interseccionId,
                         @RequestParam(required = false) String tipo,
                         Model model) {
        List<Nodo> base = interseccionId != null
                ? nodoRepository.findByInterseccion_IdOrderByTipoAscNombreAsc(interseccionId)
                : nodoRepository.findAllByOrderByInterseccion_IdAscTipoAscNombreAsc();

        List<Nodo> nodos = (tipo == null || tipo.isBlank())
                ? base
                : base.stream().filter(n -> tipo.equalsIgnoreCase(n.getTipo())).toList();

        model.addAttribute("nodos", nodos);
        model.addAttribute("intersecciones", interseccionRepository.findAllByOrderByNombreAsc());
        model.addAttribute("interseccionId", interseccionId);
        model.addAttribute("tipo", tipo);
        return "admin/nodos";
    }
}
