package br.com.contadin.application.dto.usuario;

public record UsuarioPatchRequest(
        String nome,
        String sobrenome,
        String telefone
) {
}
