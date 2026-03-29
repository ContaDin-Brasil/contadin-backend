package br.com.contadin.application.dto.usuario.auth;

public record LoginOutputDTO(
        String accessToken,
        Integer id,
        String nome,
        String sobrenome,
        String email,
        boolean ativo
) {
}
