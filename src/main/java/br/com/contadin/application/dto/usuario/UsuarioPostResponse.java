package br.com.contadin.application.dto.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UsuarioPostResponse(
        Integer id,
        String nome,
        String sobrenome,
        String email,
        String telefone,
        String senha,
        boolean ativo,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime criadoEm,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime atualizadoEm
) {
}
