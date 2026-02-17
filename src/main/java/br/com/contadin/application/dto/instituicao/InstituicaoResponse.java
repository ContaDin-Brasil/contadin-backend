package br.com.contadin.application.dto.instituicao;

import br.com.contadin.domain.enums.TipoInstituicao;

import java.time.LocalDateTime;

public record InstituicaoResponse(
        Integer id,
        String nome,
        String icone,
        String cor,
        TipoInstituicao tipo,
        boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
