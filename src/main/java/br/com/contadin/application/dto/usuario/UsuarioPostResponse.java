package br.com.contadin.application.dto.usuario;

public record UsuarioPostResponse(
        Integer id,
        String nome,
        String sobrenome,
        String email,
        String telefone,
        String senha,
        boolean ativo
) {
}
