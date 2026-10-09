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
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDate;
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
                .exceptionHandling(
                        except->
                                except
                                        .accessDeniedHandler(jwtAccessDeniedHandler)
                                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(corfConf -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("http://localhost:8080"));
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
                    configuration.setAllowedHeaders(List.of(
                            "*"
                    ));
                    configuration.setAllowCredentials(true);
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", configuration);
                    corfConf.configurationSource(source);


                });

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/auth/register",
                        "/api/v1/auth/login"
                ).permitAll()
                .anyRequest().authenticated()
        );
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();


    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @PostConstruct
    void init() {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(Usuario.builder()
                    .username("admin")
                    .nombre("Admin Principal")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .build());
            usuarioRepository.save(Usuario.builder()
                    .username("angel")
                    .nombre("Ángel Naranjo")
                    .password(passwordEncoder.encode("angel123thebest"))
                    .rol(Rol.ADMIN)
                    .build());
            usuarioRepository.save(Usuario.builder()
                    .username("luismi")
                    .nombre("Luismi López")
                    .password(passwordEncoder.encode("luismi123ermejo"))
                    .rol(Rol.ADMIN)
                    .build());
            usuarioRepository.save(Usuario.builder()
                    .username("miguel")
                    .nombre("Miguel Campos")
                    .password(passwordEncoder.encode("bocataManoloYa"))
                    .rol(Rol.ADMIN)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .username("user")
                    .nombre("Pedro")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.SOCIO)
                    .build());
        }
    }


}