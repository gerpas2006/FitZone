package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.ActividadResponseDto;
import es.safareyes.fitzone.service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/actividad")
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping("/disponibles")
    public ResponseEntity<List<ActividadResponseDto>> buscarActividadesDisponibles(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDateTime fechaHora
    ){
        if (fecha == null && fechaHora == null) {
            throw new IllegalArgumentException("Indica el parámetro fecha (yyyy-MM-dd)");
        }
        LocalDate dia = fecha != null ? fecha : fechaHora.toLocalDate();
        return ResponseEntity.ok(actividadService.actividadesDelDia(dia));
    }
}
