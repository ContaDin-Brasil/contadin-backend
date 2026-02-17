package br.com.contadin.application.dto.instituicao;

import java.time.LocalDateTime;

public record InstituicaoResponse(
        Integer id,
        String nome,
        String icone,
        String cor,
        boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
