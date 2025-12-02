package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.Infraccion;
import org.jagar.semaforosmart.repository.InfraccionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/invitado")
public class GuestController {
    private final InfraccionRepository infraccionRepository;
    public GuestController(InfraccionRepository infraccionRepository) {
        this.infraccionRepository = infraccionRepository;
    }

    @GetMapping(value = "/evidencias")
    public String evidenciasInvitado(@RequestParam(required = false) String placa, Model model) {
        List<Infraccion> infracciones = new ArrayList<>();
        String errorCarga = null;

        try {
            if (placa != null && !placa.isBlank()) {
                infracciones = infraccionRepository.findByPlacaContainingIgnoreCaseOrderByCreatedatDesc(placa);
            }
        } catch (Exception ex) {
            errorCarga = ex.getMessage();
        }

        model.addAttribute("placa", placa);
        model.addAttribute("infracciones", infracciones);
        model.addAttribute("errorCarga", errorCarga);
        model.addAttribute("modo", "invitado");
        return "invitado/consultaEvidenciasGuest";
    }
}
