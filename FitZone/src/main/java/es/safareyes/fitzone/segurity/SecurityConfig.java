package es.safareyes.fitzone.segurity;

import es.safareyes.fitzone.model.Rol;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.repository.UsuarioRepository;
import es.safareyes.fitzone.segurity.error.JwtAccessDeniedHandler;
import es.safareyes.fitzone.segurity.error.JwtAuthenticationEntryPoint;
import es.safareyes.fitzone.segurity.jwt.JwtAuthenticationFilter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter authenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(
                            List.of("http://localhost:4200")
                    );
                    configuration.setAllowedMethods(
                            List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                    );
                    configuration.setAllowedHeaders(List.of("*"));
                    configuration.setAllowCredentials(true);

                    UrlBasedCorsConfigurationSource source =
                            new UrlBasedCorsConfigurationSource();

                    source.registerCorsConfiguration("/**", configuration);
                    cors.configurationSource(source);
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Login y control de acceso sin autenticación
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/actividad/disponibles",
                                "/api/v1/plan/lista/planes"
                        ).permitAll()


                        // Recepción o administrador crean socios y su usuario
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register/socio"
                        ).hasAnyRole("ADMIN", "RECEPCION")

                        // Solo el administrador crea usuarios internos
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register/usuario"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/socio/estado/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/acceso/entrada/**",
                                "/api/v1/acceso/salida/**"
                        ).hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/socio/editar/**"
                        ).hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/cuota/pagar/**"
                        ).hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/cuota/informes/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/cuota/todas-las-cuotas",
                                "/api/v1/acceso/dentro",
                                "/api/v1/socio/listar",
                                "/api/v1/acceso/reciente",
                                "/api/v1/cuota/buscar/**",
                                "/api/v1/socio/cuotas/pendientes"
                        ).hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/socio/buscar/id/**"
                        ).hasAnyRole("ADMIN", "RECEPCION", "SOCIO")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/cuota/generar"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/acceso/contar/aforo"
                        ).hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/reserva/reservar"
                        ).hasRole("SOCIO")

                        .requestMatchers("/api/v1/socio/**")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers("/api/v1/actividad/**")
                        .hasAnyRole("ADMIN", "SOCIO")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + username
                        )
                );
    }

    @Bean
    AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider
    ) {
        return new ProviderManager(authenticationProvider);
    }

    @PostConstruct
    void init() {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(Usuario.builder()
                    .username("admin")
                    .apellidos("admin")
                    .nombre("Admin Principal")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .username("angel")
                    .nombre("Ángel")
                    .apellidos("Naranjo")
                    .password(passwordEncoder.encode("angel123thebest"))
                    .rol(Rol.ADMIN)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .username("luismi")
                    .nombre("Luismi")
                    .apellidos("López")
                    .password(passwordEncoder.encode("luismi123ermejo"))
                    .rol(Rol.ADMIN)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .username("miguel")
                    .nombre("Miguel")
                    .apellidos("Campos")
                    .password(passwordEncoder.encode("bocataManoloYa"))
                    .rol(Rol.ADMIN)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .username("user")
                    .nombre("Pedro")
                    .apellidos("Serrano")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.SOCIO)
                    .build());
        }
    }
}