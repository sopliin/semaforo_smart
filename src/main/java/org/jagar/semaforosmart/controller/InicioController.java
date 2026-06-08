package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.repository.EventoAlertaRepository;
import org.jagar.semaforosmart.repository.InterseccionRepository;
import org.jagar.semaforosmart.repository.NodoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final InterseccionRepository interseccionRepository;
    private final NodoRepository nodoRepository;
    private final EventoAlertaRepository eventoAlertaRepository;

    public InicioController(InterseccionRepository interseccionRepository,
                            NodoRepository nodoRepository,
                            EventoAlertaRepository eventoAlertaRepository) {
        this.interseccionRepository = interseccionRepository;
        this.nodoRepository = nodoRepository;
        this.eventoAlertaRepository = eventoAlertaRepository;
    }

    @GetMapping("/admin/inicio")
    public String inicio(Model model) {
        long totalIntersecciones = interseccionRepository.count();
        long interseccionesEnAlerta = interseccionRepository.countByEstado("alerta");

        long totalNodosOjo = nodoRepository.countByTipo("ojo");
        long nodosOjoOnline = nodoRepository.countByTipoAndEstadoConexion("ojo", "online");
        long totalNodosCerebro = nodoRepository.countByTipo("cerebro");
        long nodosCerebroOnline = nodoRepository.countByTipoAndEstadoConexion("cerebro", "online");

        long alertasPendientes = eventoAlertaRepository.countByEstado("pendiente");
        long alertasEnRevision = eventoAlertaRepository.countByEstado("en_revision");

        String estadoGeneral;
        if (interseccionesEnAlerta > 0) {
            estadoGeneral = "alerta";
        } else if (nodosOjoOnline < totalNodosOjo || nodosCerebroOnline < totalNodosCerebro) {
            estadoGeneral = "parcial";
        } else {
            estadoGeneral = "operativo";
        }

        model.addAttribute("totalIntersecciones", totalIntersecciones);
        model.addAttribute("interseccionesEnAlerta", interseccionesEnAlerta);
        model.addAttribute("totalNodosOjo", totalNodosOjo);
        model.addAttribute("nodosOjoOnline", nodosOjoOnline);
        model.addAttribute("totalNodosCerebro", totalNodosCerebro);
        model.addAttribute("nodosCerebroOnline", nodosCerebroOnline);
        model.addAttribute("alertasPendientes", alertasPendientes);
        model.addAttribute("alertasEnRevision", alertasEnRevision);
        model.addAttribute("estadoGeneral", estadoGeneral);
        model.addAttribute("ultimosEventos", eventoAlertaRepository.findTop6ByOrderByTsInicioDesc());

        return "admin/inicio";
    }
}
