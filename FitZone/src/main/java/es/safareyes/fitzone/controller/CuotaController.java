package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.CuotaResponseDto;
import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.service.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
