package es.safareyes.fitzone.segurity.auth.dto;

import es.safareyes.fitzone.model.Rol;
import lombok.Builder;

import java.util.Set;

@Builder
public record JwtUserResponse(
        String username,
        String token,
        Rol roles
) {
}
