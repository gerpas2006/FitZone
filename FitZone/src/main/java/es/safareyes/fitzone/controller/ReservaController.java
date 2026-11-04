package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.CrearReservaRequest;
import es.safareyes.fitzone.dto.ReservaResponseDto;
import es.safareyes.fitzone.model.Reserva;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping("/reservar")
    public ResponseEntity<ReservaResponseDto> crearReserva(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CrearReservaRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservaService.crearReserva(usuario, request));
    }
}
