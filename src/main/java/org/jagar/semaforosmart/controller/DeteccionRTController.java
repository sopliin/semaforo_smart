package org.jagar.semaforosmart.controller;

import org.jagar.semaforosmart.entity.DeteccionRT;
import org.jagar.semaforosmart.repository.DeteccionRTRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/admin/api/detecciones")
public class DeteccionRTController {

    private final DeteccionRTRepository deteccionRTRepository;

    public DeteccionRTController(DeteccionRTRepository deteccionRTRepository) {
        this.deteccionRTRepository = deteccionRTRepository;
    }

    @GetMapping("/ultimo")
    public ResponseEntity<DeteccionActualResponse> obtenerUltimo(@RequestParam(required = false) Long sitioId) {
        Optional<DeteccionRT> ultimo = sitioId != null
                ? deteccionRTRepository.findTopBySitio_IdOrderByIdDesc(sitioId)
                : deteccionRTRepository.findTopByOrderByIdDesc();

        return ultimo
                .map(det -> ResponseEntity.ok(new DeteccionActualResponse(
                        det.getTs(),
                        det.getNumvehiculos(),
                        det.getNumpeatones(),
                        det.getColoractual(),
                        det.getSegrestante()
                )))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    public record DeteccionActualResponse(java.time.LocalDateTime ts, Integer numvehiculos, Integer numpeatones, String coloractual,
                                          Short segrestante) {
    }
}