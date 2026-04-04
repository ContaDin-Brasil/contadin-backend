package br.com.contadin.application.dto.metaGasto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record MetaGastoResponse(
        UUID id,
        String nome,
        BigDecimal valor,
        Date dataFimMeta,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        Integer fkCategoria
) {
}
