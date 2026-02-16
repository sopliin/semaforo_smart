package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.Infraccion;
import org.jagar.semaforosmart.repository.InfraccionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String evidenciasInvitado(@RequestParam(required = false) String placa,
                                     @RequestParam(required = false) String tipo,
                                     Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            return "redirect:/admin/evidencias";
        }
        List<Infraccion> infracciones = new ArrayList<>();
        String errorCarga = null;

        try {
            if (placa != null && !placa.isBlank()) {
                String placaNormalizada = normalizarPlaca(placa);
                infracciones = infraccionRepository.findByPlacaContainingIgnoreCaseOrderByCreatedatDesc(placa);
                if (!placaNormalizada.isBlank()) {
                    infracciones = infracciones.stream()
                            .filter(inf -> normalizarPlaca(inf.getPlaca()).contains(placaNormalizada))
                            .toList();
                }
                if (tipo != null && !tipo.isBlank()) {
                    infracciones = infracciones.stream()
                            .filter(inf -> tipo.equalsIgnoreCase(inf.getTipo()))
                            .toList();
                }
            }
        } catch (Exception ex) {
            errorCarga = ex.getMessage();
        }

        model.addAttribute("placa", placa);
        model.addAttribute("tipo", tipo);
        model.addAttribute("infracciones", infracciones);
        model.addAttribute("errorCarga", errorCarga);
        model.addAttribute("modo", "invitado");
        return "invitado/consultaEvidenciasGuest";
    }

    private String normalizarPlaca(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[^A-Za-z0-9]", "").toUpperCase(java.util.Locale.ROOT);
    }
}
