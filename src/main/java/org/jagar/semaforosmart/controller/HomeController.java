package org.jagar.semaforosmart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class HomeController {

    @GetMapping({"/", "/inicio"})
    public String landing(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMensaje", error);
        }
        return "index";
    }

    @GetMapping("/admin/login")
    public String adminLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            return "redirect:/admin/inicio";
        }
        if (error != null) {
            model.addAttribute("errorMensaje", "Credenciales inválidas, inténtelo nuvamente.");
        }
        model.addAttribute("mostrarLogin", true);
        return "index";
    }
}