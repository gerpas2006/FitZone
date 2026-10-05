package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.AccesoResponseDto;
import es.safareyes.fitzone.service.AccesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/acceso")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    @PostMapping("/entrada/{dni}")
    public ResponseEntity<Boolean> tornoEntrada(@PathVariable String dni){
        boolean resultado = accesoService.tornoEntrada(dni);
        return ResponseEntity.ok(resultado);
    }
}
