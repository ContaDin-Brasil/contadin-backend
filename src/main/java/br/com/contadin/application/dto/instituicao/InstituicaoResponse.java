package br.com.contadin.application.dto.instituicao;

import br.com.contadin.domain.enums.TipoInstituicao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InstituicaoResponse(
        UUID id,
        String nome,
        String icone,
        String cor,
        TipoInstituicao tipo,
        BigDecimal saldoInicial,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
