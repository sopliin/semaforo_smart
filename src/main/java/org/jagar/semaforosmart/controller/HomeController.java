package org.jagar.semaforosmart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @GetMapping(value = "/evidencias")
    public String consultas() {
        return "admin/consultaEvidencias";
    }
    @GetMapping(value = "/gestion")
    public String gestion() {
        return "admin/gestionSemaforica";
    }
}

