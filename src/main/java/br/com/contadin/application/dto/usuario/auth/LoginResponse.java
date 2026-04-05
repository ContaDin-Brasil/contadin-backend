package br.com.contadin.application.dto.usuario.auth;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        UUID id,
        String nome,
        String sobrenome,
        String email,
        boolean ativo
) {}
