package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.ReservaResponseDto;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping("/{id}/reservas")
    public ResponseEntity<ReservaResponseDto> reservarActividad(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        ReservaResponseDto respuesta = ReservaResponseDto.of(
                reservaService.reservarActividad(id, usuario)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }
}
