package br.com.contadin.application.dto.instituicao;

import br.com.contadin.domain.enums.TipoInstituicao;

public record InstituicaoRequest(
        String nome,
        String icone,
        String cor,
        TipoInstituicao tipo
) {
}
