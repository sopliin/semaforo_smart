package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.model.SemaforoDetalle;
import org.jagar.semaforosmart.repository.SemaforoDetalleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                request.modoAutomatico())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semáforo no encontrado"));
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
        if (request.modoAutomatico() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica el modo de operación");
        }
    }

    public record TiemposRequest(Integer rojo, Integer amarillo, Integer verde, Integer peatonal, Boolean modoAutomatico) {

    }
}