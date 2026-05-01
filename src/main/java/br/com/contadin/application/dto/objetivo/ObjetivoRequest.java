package br.com.contadin.application.dto.objetivo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record ObjetivoRequest(
        String nome,
        BigDecimal valor,
        Date dataFimObjetivo,
        UUID fkCategoria,
        UUID fkUsuario
) {
}
