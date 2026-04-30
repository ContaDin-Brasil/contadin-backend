package br.com.contadin.application.dto.instituicao;

import br.com.contadin.domain.enums.TipoInstituicao;

import java.util.UUID;

public record InstituicaoRequest(
        String nome,
        String icone,
        String cor,
        TipoInstituicao tipo,
        Boolean ativo,
        UUID fkUsuario
) {
}
