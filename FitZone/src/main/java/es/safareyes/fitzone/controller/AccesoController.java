package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.AccesoResponseDto;
import es.safareyes.fitzone.service.AccesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/acceso")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    @PostMapping("/entrada/{dni}")
    public ResponseEntity<AccesoResponseDto> tornoEntrada(@PathVariable String dni) {
        return ResponseEntity.ok(
                AccesoResponseDto.of(accesoService.tornoEntrada(dni))
        );
    }

    @PostMapping("/salida/{dni}")
    public ResponseEntity<AccesoResponseDto> tornoSalida(@PathVariable String dni) {
        return ResponseEntity.ok(
                AccesoResponseDto.of(accesoService.tornoSalida(dni))
        );
    }

    @GetMapping("/contar/aforo")
    public ResponseEntity<Long> contarAforo(){
        return ResponseEntity.ok(accesoService.personaDentroGym());
    }
}
