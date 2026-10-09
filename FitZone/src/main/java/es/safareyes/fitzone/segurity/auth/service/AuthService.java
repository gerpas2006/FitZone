package es.safareyes.fitzone.segurity.auth.service;

import es.safareyes.fitzone.model.Rol;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.repository.UsuarioRepository;
import es.safareyes.fitzone.segurity.auth.dto.JwtUserResponse;
import es.safareyes.fitzone.segurity.auth.dto.LoginRequest;
import es.safareyes.fitzone.segurity.auth.dto.RegisterRequest;
import es.safareyes.fitzone.segurity.jwt.JwtAccessTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtAccessTokenService jwtService;

    @Transactional
    public JwtUserResponse register (RegisterRequest request) {

        if (usuarioRepository.existsByUsername(request.username())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya existe");
        }

        Usuario user = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .rol(Rol.SOCIO)
                .build();

        usuarioRepository.save(user);

        return login(new LoginRequest(user.getUsername(), request.password()));
    }

    @Transactional
    public JwtUserResponse login (LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Usuario user = usuarioRepository.findByUsername(auth.getName()).get();
        String token = jwtService.generateAccessToken(user.getId().toString());

        return JwtUserResponse.builder()
                .username(user.getUsername())
                .roles(user.getRol())
                .token(token)
                .build();
    }

}