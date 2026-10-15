package es.safareyes.fitzone.segurity.auth.service;

import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.Plan;
import es.safareyes.fitzone.model.Rol;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.repository.PlanRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import es.safareyes.fitzone.repository.UsuarioRepository;
import es.safareyes.fitzone.segurity.auth.dto.JwtUserResponse;
import es.safareyes.fitzone.segurity.auth.dto.LoginRequest;
import es.safareyes.fitzone.segurity.auth.dto.RegisterSocioRequest;
import es.safareyes.fitzone.segurity.auth.dto.RegisterUsuarioRequest;
import es.safareyes.fitzone.segurity.jwt.JwtAccessTokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SocioRepository socioRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtAccessTokenService jwtService;

    @Transactional
    public Usuario registerSocio(RegisterSocioRequest request) {
        validarUsernameDisponible(request.username());

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado ningún plan con ese id"
                ));

        if (socioRepository.findByDni(request.dni()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El DNI ya está registrado"
            );
        }

        if (socioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El email ya está registrado"
            );
        }

        Socio socio = Socio.builder()
                .dni(request.dni())
                .email(request.email())
                .telefono(request.telefono())
                .fechaAlta(LocalDate.now())
                .matricula(request.matricula())
                .estado(Estado.ACTIVO)
                .plan(plan)
                .build();

        socioRepository.save(socio);

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.SOCIO)
                .socio(socio)
                .build();

        socio.setUsuario(usuario);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario registerUsuario(RegisterUsuarioRequest request) {
        validarUsernameDisponible(request.username());

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.RECEPCION)
                .build();

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public JwtUserResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        Usuario user = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado el usuario"
                ));

        String token = jwtService.generateAccessToken(
                user.getId().toString()
        );

        return JwtUserResponse.builder()
                .username(user.getUsername())
                .roles(user.getRol())
                .token(token)
                .build();
    }

    private void validarUsernameDisponible(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya existe"
            );
        }
    }
}