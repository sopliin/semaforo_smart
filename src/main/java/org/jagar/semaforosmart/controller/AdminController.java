package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.Infraccion;
import org.jagar.semaforosmart.entity.Semaforo;
import org.jagar.semaforosmart.entity.Sitio;
import org.jagar.semaforosmart.repository.InfraccionRepository;
import org.jagar.semaforosmart.repository.SitioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final InfraccionRepository infraccionRepository;
    private final SitioRepository sitioRepository;

    public AdminController(InfraccionRepository infraccionRepository, SitioRepository sitioRepository) {
        this.infraccionRepository = infraccionRepository;
        this.sitioRepository = sitioRepository;
    }

    @GetMapping(value = "/evidencias")
    public String evidenciasAdmin(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long sitioId,
            @RequestParam(required = false, name = "desde") String fechaDesde,
            @RequestParam(required = false, name = "hasta") String fechaHasta,
            Model model) {
        List<Infraccion> infracciones = new ArrayList<>();
        List<Sitio> sitios = new ArrayList<>();
        String errorCarga = null;

        try {
            infracciones = infraccionRepository.findAll();
            sitios = sitioRepository.findAll();
        } catch (Exception e) {
            errorCarga = e.getMessage();
        }

        LocalDate desde = parseFechaTexto(fechaDesde);
        LocalDate hasta = parseFechaTexto(fechaHasta);
        LocalDate hastaFinal = (fechaHasta == null || fechaHasta.isBlank()) ? LocalDate.now() : hasta;

        List<Infraccion> filtradas = infracciones.stream()
                .sorted(Comparator.comparing(Infraccion::getCreatedat, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .filter(inf -> filtroPlaca(inf, placa))
                .filter(inf -> filtroTipo(inf, tipo))
                .filter(inf -> filtroSitio(inf, sitioId))
                .filter(inf -> filtroFecha(inf, desde, hastaFinal))
                .collect(Collectors.toList());
        model.addAttribute("infracciones", filtradas);
        model.addAttribute("placa", placa);
        model.addAttribute("tipo", tipo);
        model.addAttribute("sitioId", sitioId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("sitios", sitios);
        model.addAttribute("errorCarga", errorCarga);
        model.addAttribute("modo", "admin");
        return "admin/consultaEvidencias";
    }

    @GetMapping(value = "/gestion")
    public String gestion(Model model){
        model.addAttribute("sitios", sitioRepository.findAll());
        return "admin/gestionSemaforica";
    }

    private boolean filtroPlaca(Infraccion infraccion, String placa){
        if (placa == null || placa.isBlank()){
            return true;
        }
        String placaNormalizada = normalizarPlaca(placa);
        if (placaNormalizada.isEmpty()){
            return true;
        }
        return Optional.ofNullable(infraccion.getPlaca())
                .map(this::normalizarPlaca)
                .filter(p -> !p.isEmpty())
                .map(p -> p.contains(placaNormalizada))
                .orElse(false);
    }

    private boolean filtroTipo(Infraccion infraccion, String tipo){
        if (tipo == null || tipo.isBlank()){
            return true;
        }
        return tipo.equalsIgnoreCase(Optional.ofNullable(infraccion.getTipo()).orElse(""));
    }

    private boolean filtroSitio(Infraccion infraccion, Long sitioId){
        if (sitioId == null){
            return true;
        }
        return Optional.ofNullable(infraccion.getSemaforo())
                .map(Semaforo::getSitio)
                .map(Sitio::getId)
                .map(id -> Objects.equals(id, sitioId))
                .orElse(false);
    }

    private boolean filtroFecha(Infraccion infraccion, LocalDate desde, LocalDate hasta){
        if (desde == null && hasta == null){
            return true;
        }
        LocalDate fechaInfraccion = toLocalDate(infraccion.getCreatedat());
        if (fechaInfraccion == null) {
            return false;
        }
        boolean despuesDe = desde == null || !fechaInfraccion.isBefore(desde);
        boolean antesDe = hasta == null || !fechaInfraccion.isAfter(hasta);
        return despuesDe && antesDe;
    }

    private LocalDate parseFechaTexto(String valor){
        if (valor == null || valor.isBlank()){
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDate.parse(valor.substring(0, Math.min(valor.length(), 10)));
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }

    private LocalDate toLocalDate(LocalDateTime valor){
        return valor != null ? valor.toLocalDate() : null;
    }

    private String normalizarPlaca(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase(Locale.ROOT);
    }
}
