package br.com.contadin.application.dto.objetivo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record ObjetivoResponse(
        UUID id,
        String nome,
        BigDecimal valor,
        Date dataFimObjetivo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        UUID fkCategoria
) {
}
