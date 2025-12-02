package org.jagar.semaforosmart.controller;

//import org.jagar.semaforosmart.entity.Infraccion;
//import org.jagar.semaforosmart.entity.Sitio;
//import org.jagar.semaforosmart.repository.InfraccionRepository;
//import org.jagar.semaforosmart.repository.SitioRepository;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Controller
//public class HomeController {
//    private final InfraccionRepository infraccionRepository;
//    private final SitioRepository sitioRepository;
//
//    public HomeController(InfraccionRepository infraccionRepository,  SitioRepository sitioRepository) {
//        this.infraccionRepository = infraccionRepository;
//        this.sitioRepository = sitioRepository;
//    }
//
//    @GetMapping(value = "/evidencias")
//    public String evidenciasAdmin(
//            @RequestParam(required = false) String placa,
//            @RequestParam(required = false) String tipo,
//            @RequestParam(required = false) Integer sitioId,
//            @RequestParam(required = false, name = "desde") String fechaDesde,
//            @RequestParam(required = false, name = "hasta") String fechaHasta,
//            Model model
//    ) {
//        List<Infraccion> infracciones = new ArrayList<>();
//        List<Sitio> sitios = new ArrayList<>();
//        String errorCarga = null;
//
//        try {
//            infracciones = infraccionRepository.findAll();
//            sitios = sitioRepository.findAll();
//        } catch (Exception e) {
//            errorCarga = "No se pudieron obtener los datos desde la base de datos";
//        }
//
//        LocalDate desde = parseFecha(fechaDesde);
//        LocalDate hasta = parseFecha(fechaHasta);
//
//        List<Infraccion> filtradas = infracciones.stream()
//                .sorted(Comparator.comparing(Infraccion::getCreatedat, Comparator.nullsLast(String::compareTo)).reversed())
//                .filter(inf -> filtroPlaca(inf, placa))
//                .filter(inf -> filtroTipo(inf, tipo))
//                .filter(inf -> filtroSitio(inf, sitioId))
//                .filter(inf -> filtroFecha(inf, desde, hasta))
//                .collect(Collectors.toList());
//
//        model.addAttribute("infracciones",  filtradas);
//        model.addAttribute("placa",  placa);
//        model.addAttribute("tipo",  tipo);
//        model.addAttribute("sitioId",  sitioId);
//        model.addAttribute("fechaDesde",  fechaDesde);
//        model.addAttribute("fechaHasta",  fechaHasta);
//        model.addAttribute("sitios",  sitios);
//        model.addAttribute("errorCarga", errorCarga);
//        model.addAttribute("modo", "admin");
//        return "admin/consultaEvidencias";
//    }
//
//    @GetMapping(value = "/invitado/evidencias")
//    public String evidenciasInvitado(@RequestParam(required = false) String placa,
//                                     Model model) {
//        List<Infraccion> infracciones = new ArrayList<>();
//        String errorCarga = null;
//
//        try {
//            if (placa != null && !placa.isBlank()) {
//                infracciones = infraccionRepository.findByPlacaContainingIgnoreCaseOrderByCreatedatDesc(placa);
//            }
//        } catch (Exception e) {
//            errorCarga = "No se pudieron obtener los datos desde la base de datos";
//        }
//
//        model.addAttribute("infracciones", infracciones);
//        model.addAttribute("placa", placa);
//        model.addAttribute("modo", "invitado");
//        model.addAttribute("errorCarga", errorCarga);
//        return "invitado/consultaEvidenciasGuest";
//    }
//
//    @GetMapping(value = "/gestion")
//    public String gestion() {
//        return "admin/gestionSemaforica";
//    }
//
//    private boolean filtroPlaca(Infraccion infraccion, String placa) {
//        if (placa == null || placa.isBlank()) {
//            return true;
//        }
//        return Optional.ofNullable(infraccion.getPlaca())
//                .map(p -> p.toLowerCase(Locale.ROOT).contains(placa.toLowerCase(Locale.ROOT)))
//                .orElse(false);
//    }
//
//    private boolean filtroTipo(Infraccion infraccion, String tipo) {
//        if (tipo == null || tipo.isBlank()) {
//            return true;
//        }
//        return tipo.equalsIgnoreCase(Optional.ofNullable(infraccion.getTipo()).orElse(""));
//    }
//
//    private boolean filtroSitio(Infraccion infraccion, Integer sitioId) {
//        if (sitioId == null) {
//            return true;
//        }
//        return Optional.ofNullable(infraccion.getSitio())
//                .map(Sitio::getId)
//                .map(id -> id == sitioId)
//                .orElse(false);
//    }
//
//    private boolean filtroFecha(Infraccion infraccion, LocalDate desde, LocalDate hasta) {
//        if (desde == null && hasta == null) {
//            return true;
//        }
//        LocalDate fechaInfraccion = parseFecha(infraccion.getCreatedat());
//        if (fechaInfraccion == null) {
//            return false;
//        }
//        boolean despuesDe = desde == null || !fechaInfraccion.isBefore(desde);
//        boolean antesDe = hasta == null || !fechaInfraccion.isAfter(hasta);
//        return despuesDe && antesDe;
//    }
//
//    private LocalDate parseFecha(String valor) {
//        if (valor == null || valor.isBlank()) {
//            return null;
//        }
//        try {
//            return LocalDate.parse(valor);
//        } catch (DateTimeParseException ignored) {
//        }
//        try {
//            return LocalDate.parse(valor.substring(0, Math.min(valor.length(), 10)));
//        } catch (DateTimeParseException ignored) {
//        }
//        return null;
//    }
//}
//

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    @Value("${admin.username:admin}")
    private String adminUsername;
    @Value("${admin.password:admin}")
    private String adminPassword;

    @GetMapping({"/", "/inicio"})
    public String landing(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMensaje", error);
        }
        return "index";
    }

    @PostMapping("/admin/login")
    public String loginAdmin(@RequestParam String username,
                             @RequestParam String password,
                             RedirectAttributes redirectAttributes) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            return "redirect:/admin/evidencias";
        }
        redirectAttributes.addAttribute("error", "Credenciales inválidas. Inténtelo nuevamente.");
        return "redirect:/";
    }
}