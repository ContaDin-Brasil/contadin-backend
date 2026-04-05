package br.com.contadin.application.dto.metaGasto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record MetaGastoRequest(
        String nome,
        BigDecimal valor,
        Date dataFimMeta,
        UUID fkCategoria,
        UUID fkUsuario
) {
}
