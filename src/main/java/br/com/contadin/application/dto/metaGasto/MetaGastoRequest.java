package br.com.contadin.application.dto.metaGasto;

import java.math.BigDecimal;
import java.util.Date;

public record MetaGastoRequest(
        String nome,
        BigDecimal valor,
        Date dataFimMeta,
        Integer fkCategoria,
        Integer fkUsuario
) {
}
