package br.com.contadin.application.dto.usuario;

public record AtualizarUsuarioRequest(
        String nome,
        String sobrenome,
        String telefone
) {}
