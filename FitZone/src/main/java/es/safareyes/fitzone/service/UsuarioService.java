package es.safareyes.fitzone.service;

import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
}
