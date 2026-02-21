package br.com.contadin.application.dto.metaGasto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public record MetaGastoResponse(
        Integer id,
        String nome,
        BigDecimal valor,
        Date dataFimMeta,
        LocalDateTime criadoEm,
        Integer fkCategoria
) {
}
