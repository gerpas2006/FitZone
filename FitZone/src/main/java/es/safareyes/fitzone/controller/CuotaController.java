package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.CuotaResponseDto;
import es.safareyes.fitzone.dto.IngresosPorMesPlan;
import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.service.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cuota")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;

    @PostMapping("/pagar/{id}")
    public ResponseEntity<CuotaResponseDto> pagarCuota(@PathVariable UUID id){
        return ResponseEntity.ok(CuotaResponseDto.of(cuotaService.registrarPago(id)));
    }

    @GetMapping("/informes/ingresos")
    public ResponseEntity<List<IngresosPorMesPlan>> obtenerIngresos(
            @RequestParam Integer anio
    ) {
        return ResponseEntity.ok(
                cuotaService.obtenerIngresosPorMesYPlan(anio)
        );
    }




}
