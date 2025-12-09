package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.model.SemaforoDetalle;
import org.jagar.semaforosmart.repository.SemaforoDetalleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/api/semaforos")
public class GestionSemaforoController {

    private final SemaforoDetalleRepository semaforoDetalleRepository;

    public GestionSemaforoController(SemaforoDetalleRepository semaforoDetalleRepository){
        this.semaforoDetalleRepository = semaforoDetalleRepository;
    }

    @GetMapping
    public List<SemaforoDetalle> listar(@RequestParam(required = false) Long sitioId) {
        return semaforoDetalleRepository.findAll(sitioId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SemaforoDetalle> obtener(@PathVariable Long id){
        return semaforoDetalleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public SemaforoDetalle actualizar(@PathVariable Long id, @RequestBody TiemposRequest request){
        validar(request);
        return semaforoDetalleRepository.updateTiempos(
               Math.toIntExact(id),
                request.rojo(),
                request.amarillo(),
                request.verde(),
                request.peatonal(),
                request.modoPrioridad())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semáforo no encontrado"));
    }

    @PostMapping
    public ResponseEntity<SemaforoDetalle> crear(@RequestBody NuevaConfiguracionRequest request){
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud vacía");
        }
        if (request.rojo() == null || request.amarillo() == null || request.verde() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completa los tiempos vehiculares");
        }
        if (request.rojo() < 0 || request.amarillo() < 0 || request.verde() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los tiempos no pueden ser negativos");
        }

        BigDecimal speed = request.speedLimitKmh();
        if (speed != null && BigDecimal.ZERO.compareTo(speed) == 0) {
            speed = null;
        }

        try {
            SemaforoDetalle creado = semaforoDetalleRepository.crearConfiguracion(
                            request.sitioId(),
                            request.nombre(),
                            request.rojo(),
                            request.amarillo(),
                            request.verde(),
                            speed)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Sitio no encontrado para registrar la configuración"));

            return ResponseEntity.status(HttpStatus.CREATED).body(creado);

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Error al guardar la configuración: " + ex.getMostSpecificCause().getMessage(),
                    ex);
        }
    }

    private void validar(TiemposRequest request) {
        if(request == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se recibieron datos para actualizar");
        }
        if (request.rojo() == null || request.amarillo() == null || request.verde() == null || request.peatonal() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos los tiempos son obligatorios");
        }
        if (request.rojo() < 0 || request.amarillo() < 0 || request.verde() < 0 || request.peatonal() < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los tiempos no pueden ser negativos");
        }
        if (request.modoPrioridad() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica el modo de operación");
        }
    }

    public record TiemposRequest(Integer rojo, Integer amarillo, Integer verde, Integer peatonal, Boolean modoPrioridad) {

    }

    public record NuevaConfiguracionRequest(Long sitioId, String nombre, Integer rojo, Integer amarillo,
                                            Integer verde, BigDecimal speedLimitKmh) {
    }
}