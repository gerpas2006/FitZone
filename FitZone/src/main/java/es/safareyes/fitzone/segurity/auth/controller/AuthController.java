package es.safareyes.fitzone.segurity.auth.controller;

import es.safareyes.fitzone.dto.UsuarioResponseDto;
import es.safareyes.fitzone.segurity.auth.dto.JwtUserResponse;
import es.safareyes.fitzone.segurity.auth.dto.LoginRequest;
import es.safareyes.fitzone.segurity.auth.dto.RegisterSocioRequest;
import es.safareyes.fitzone.segurity.auth.dto.RegisterUsuarioRequest;
import es.safareyes.fitzone.segurity.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtUserResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/register/socio")
    public ResponseEntity<UsuarioResponseDto> registerSocio(
            @Valid @RequestBody RegisterSocioRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        UsuarioResponseDto.of(
                                authService.registerSocio(request)
                        )
                );
    }

    @PostMapping("/register/usuario")
    public ResponseEntity<UsuarioResponseDto> registerUsuario(
            @Valid @RequestBody RegisterUsuarioRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        UsuarioResponseDto.of(
                                authService.registerUsuario(request)
                        )
                );
    }
}